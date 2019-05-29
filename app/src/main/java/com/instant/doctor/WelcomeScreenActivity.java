package com.instant.doctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.instant.doctor.Adapters.SliderAdapter;

public class WelcomeScreenActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private LinearLayout dotLinearLayout;
    private SliderAdapter sliderAdapter;
    private TextView[] mDots;
    private Button prev_btn;
    private Button next_btn;

    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);


        viewPager=findViewById(R.id.viewPager);
        dotLinearLayout=findViewById(R.id.dot_layout);
        sliderAdapter=new SliderAdapter(this);

        viewPager.setAdapter(sliderAdapter);

        viewPager.addOnPageChangeListener(viewListener);

        addDotsIndicator(0);

    }
    public void addDotsIndicator(int position){
        mDots=new TextView[2];
        for (int i=0 ;i<mDots.length;i++){
            mDots[i]=new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransaprentWhite));

            dotLinearLayout.addView(mDots[i]);
        }
        if (mDots.length>0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }
    ViewPager.OnPageChangeListener viewListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
            mCurrentPage=i;
            if (i==0){
                next_btn.setEnabled(true);
                prev_btn.setEnabled(false);
                prev_btn.setVisibility(View.INVISIBLE);

                next_btn.setText("Next");
                prev_btn.setText("");
            }else if (i==mDots.length-1){
                next_btn.setEnabled(true);
                prev_btn.setEnabled(true);
                prev_btn.setVisibility(View.VISIBLE);

                next_btn.setText("Finish");
                prev_btn.setText("Back");
            }else {
                next_btn.setEnabled(true);
                prev_btn.setEnabled(true);
                prev_btn.setVisibility(View.VISIBLE);

                next_btn.setText("Next");
                prev_btn.setText("Back");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
