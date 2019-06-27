package com.instant.doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.instant.doctor.models.DoctorInfo;
import com.instant.doctor.models.Referral;
import com.instant.doctor.models.PatientInfo;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;

public class LabReferralActivity extends AppCompatActivity {


    private static final String TAG = "Referral";
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);

    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.DARK_GRAY);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 14,
            Font.BOLD);


    EditText notesEt;
    Button saveBtn;
    ProgressDialog progressDialog;


    private String sessionId;
    private PatientInfo patientInfo;
    private DoctorInfo doctorInfo;
    private boolean isLab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_referral);

        if (getIntent() != null) {
            sessionId = getIntent().getStringExtra("sessionId");
            isLab = getIntent().getBooleanExtra("isLab", false);
            patientInfo = (PatientInfo) getIntent().getSerializableExtra("patient");
            doctorInfo = (DoctorInfo) getIntent().getSerializableExtra("doctor");

        }

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        String title = isLab ? "Lab Referral" : "Hospital Referral";
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView textView = findViewById(R.id.title);
        String msg= isLab ? "Generate Lab Referral Note": "Generate Hospital Referral Note";
        textView.setText(msg);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        notesEt = findViewById(R.id.ref_notes);
        saveBtn = findViewById(R.id.save_button);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(notesEt.getText().toString())) {
                    return;
                }

                saveLabReferral();
            }
        });

    }

    private void saveLabReferral() {
        String notes = notesEt.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //create pdf,
        String filePath = createPdf();

        String doctorName = doctorInfo.getName();

        Referral referral = new Referral(sessionId, patientInfo.getId(), doctorName, notes, new Date().getTime());
        if (isLab) {
            referral.setLab(true);
        } else {
            referral.setLab(false);
        }

        uploadData(db, filePath, referral);

    }


    private void saveLabReferral(FirebaseFirestore db, Referral referral, String url) {
        referral.setNotesUrl(url);
        db.collection("referrals")
                .add(referral)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Do something
                        progressDialog.dismiss();

                        Toast.makeText(LabReferralActivity.this, " Referral Send Successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.e(TAG, "Data saving failed: ", e);
            }
        });
    }

    private void uploadData(FirebaseFirestore db, String filePath, Referral referral) {

        progressDialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        Uri file = Uri.fromFile(new File(filePath));


        StorageReference labRef = storageRef.child("referrals/" + file.getLastPathSegment());
        UploadTask uploadTask = labRef.putFile(file);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return labRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    saveLabReferral(db, referral, downloadUri.toString());


                } else {
                    // Handle failures
                    // ...
                    progressDialog.dismiss();
                    Toast.makeText(LabReferralActivity.this, "Saving the Note Failed, Please Try again", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "File Upload Failed: ", e);
            }
        });


    }


    private String createPdf() {

        String pdfile = "Lab_" + sessionId + ".pdf";
        File file = new File(getCacheDir(), pdfile);

        try {

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            addMetaData(document);
            addContent(document);
            document.close();

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }

        return file.getPath();
    }

    private void addContent(Document document) throws DocumentException {

        PdfPTable table = new PdfPTable(2);
        table.setWidths(new float[]{25, 75});


        PdfPCell c1 = new PdfPCell(new Phrase("Patient Name"));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(": " + patientInfo.getName()));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(c1);

//        int age =
        Date dob = new Date(patientInfo.getDate());
        Date currentDate = new Date();
        int age = currentDate.getYear() - dob.getYear();

        c1 = new PdfPCell(new Phrase("Age"));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(": " + age));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Date"));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(": " + new Date().toString()));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(c1);


        c1 = new PdfPCell(new Phrase("Doctors Name"));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(": " + doctorInfo.getName()));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(c1);

        table.setHorizontalAlignment(Element.ALIGN_LEFT);


        Paragraph preface = new Paragraph();

        String titleSri = isLab ? "Lab Referral Notes" : "Hospital Referral";
        Paragraph title = new Paragraph(titleSri, catFont);
        preface.add(title);
        addEmptyLine(preface, 1);

        Paragraph subTile = new Paragraph("Notes", smallBold);

        Paragraph content = new Paragraph(notesEt.getText().toString(), subFont);


        preface.add(table);

        // LINE SEPARATOR
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

        preface.add(new Chunk(lineSeparator));

        addEmptyLine(new Paragraph(), 2);

        preface.add(subTile);
        preface.add(content);
        addEmptyLine(new Paragraph(), 2);

        document.add(preface);

    }

    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    // iText allows to add metadata to the PDF which can be viewed in your Adobe
    // Reader
    // under File -> Properties
    private void addMetaData(Document document) {
        document.addTitle("My first PDF");
        document.addSubject("Using iText");
        document.addKeywords("Java, PDF, iText");
        document.addAuthor("Lars Vogel");
        document.addCreator("Lars Vogel");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
