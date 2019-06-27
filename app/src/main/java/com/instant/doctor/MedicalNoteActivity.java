package com.instant.doctor;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.instant.doctor.models.DoctorInfo;
import com.instant.doctor.models.MedicalNote;
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
import java.io.Serializable;
import java.util.Date;

public class MedicalNoteActivity extends AppCompatActivity {
    private EditText et_diagnosis;
    private EditText et_prescription;
    private TextView tv_patientname;
    private Button save_button;

    private ImageButton cancel_button;

    private FirebaseUser user;
    private FirebaseFirestore db;

    private String sessionId;
    private PatientInfo patientInfo;
    private DoctorInfo doctorInfo;

    ProgressDialog progressDialog;
    public static final String TAG = "PrescriptionDiagnosis";

    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);

    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.DARK_GRAY);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 14,
            Font.BOLD);



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagnosis_prescription_fragment);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);


        getSupportActionBar().setTitle("Medical Note");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (getIntent().hasExtra("sessionId")) {
            sessionId = getIntent().getStringExtra("sessionId");
            patientInfo = (PatientInfo) getIntent().getSerializableExtra("patient");
            doctorInfo = (DoctorInfo) getIntent().getSerializableExtra("doctor");

        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);


        et_diagnosis = findViewById(R.id.textArea_diagnosis);
        et_prescription = findViewById(R.id.textArea_prescription);
        tv_patientname = findViewById(R.id.patient_name);
        save_button = findViewById(R.id.diagnosis_button);

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something

                saveDiagnosis();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveDiagnosis() {

        String diagnosis = et_diagnosis.getText().toString();
        String prescription = et_prescription.getText().toString();

        if (TextUtils.isEmpty(diagnosis) || TextUtils.isEmpty(diagnosis)) {
            Toast.makeText(this, "Fill all the values", Toast.LENGTH_LONG).show();
            return;
        }


        user = FirebaseAuth.getInstance().getCurrentUser();


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //create pdf,
        String filePath = createPdf();
        long time = new Date().getTime();
        String doctorName = doctorInfo.getName();



        MedicalNote medicalNote = new MedicalNote(sessionId, diagnosis, prescription, time, doctorName);
        medicalNote.setPatientId(patientInfo.getId());


        uploadData(db, filePath, medicalNote);

        //delete the file
        File file = new File(filePath);
        file.delete();


    }

    private void saveMedicalNote(FirebaseFirestore db, MedicalNote medicalNote, String url) {
        medicalNote.setMedicalNoteUrl(url);
        db.collection("medicalNotes")
                .add(medicalNote)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Do something
                        progressDialog.dismiss();
                        Toast.makeText(MedicalNoteActivity.this, "Medical Note Ready", Toast.LENGTH_LONG).show();
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

    private void uploadData(FirebaseFirestore db, String filePath, MedicalNote medicalNote) {

        progressDialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        Uri file = Uri.fromFile(new File(filePath));
        Log.d(TAG, "uploadData File " + file.toString());
        //
//        return;

        StorageReference medicalNotesRef = storageRef.child("medicalNotes/" + file.getLastPathSegment());
        UploadTask uploadTask = medicalNotesRef.putFile(file);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return medicalNotesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    Log.d(TAG, "File Upload Data Url: " + downloadUri.toString());
                    saveMedicalNote(db, medicalNote, downloadUri.toString());


                } else {
                    // Handle failures
                    // ...
                    progressDialog.dismiss();
                    Toast.makeText(MedicalNoteActivity.this, "Saving the Note Failed, Please Try again", Toast.LENGTH_LONG).show();
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

        String pdfile = "MedicalNote_" + sessionId + ".pdf";
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

        Paragraph title = new Paragraph("Medical Note", catFont);
        preface.add(title);
        addEmptyLine(preface, 1);

        Paragraph subTile = new Paragraph("Diagnosis", smallBold);

        Paragraph content = new Paragraph(et_diagnosis.getText().toString(), subFont);


        preface.add(table);

        // LINE SEPARATOR
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

        preface.add(new Chunk(lineSeparator));

        addEmptyLine(new Paragraph(), 2);

        preface.add(subTile);
        preface.add(content);
        addEmptyLine(new Paragraph(), 2);

        subTile = new Paragraph("Prescription", smallBold);
        content = new Paragraph(et_prescription.getText().toString(), subFont);
        preface.add(subTile);
        preface.add(content);


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

}
