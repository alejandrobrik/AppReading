package com.example.appreading.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appreading.R;
import com.example.appreading.models.Lecture;
import com.example.appreading.models.Student;
import com.example.appreading.utils.MoreUtils;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class LectureAdapter extends RecyclerView.Adapter<LectureAdapter.LectureViewHolder> {


    private List<Lecture> data = new ArrayList<>();
    private List<Lecture> originalData = new ArrayList<>();
    private Context context;


    public LectureAdapter() {
    }

    public void setData(List<Lecture> data) {
        this.data = data;
        this.originalData.addAll(data);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public LectureAdapter.LectureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new LectureAdapter.LectureViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview_lecture, parent, false));
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull LectureViewHolder holder, int position) {
        Lecture lecture = data.get(position);


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()); // Define el formato deseado
        String formattedDate = dateFormat.format(lecture.getDateLecture().toDate());

        holder.tvDate.setText(MoreUtils.coalesce(formattedDate, "N/D"));
        holder.tvNamePDF.setText(MoreUtils.coalesce(lecture.getNamePDF(), "N/D"));
        holder.tvPercent.setText(MoreUtils.coalesce(lecture.getPercentLecture() +"%", "N/D"));




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putSerializable("Course", new Gson().toJson(data.get(holder.getAdapterPosition())));

                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /*               Navigation.findNavController(view).navigate(R.id.action_nav_course_to_studentListFragment,b);*/
            }
        });

/*        Glide.with(context)
                .load(student.getPhoto_url())
                .error(R.drawable.ic_user)
                .into(holder.imgStudent);*/

/*        if (Course.getGender() != null){
        if (Course.getGender().equals("Female")) {
            Glide.with(context)
                    .load(Course.getUrlImage())
                    .error(R.drawable.ic_female)
                    .into(holder.imgCourse);
        }
        if (Course.getGender().equals("Male")){
            Glide.with(context)
                    .load(Course.getUrlImage())
                    .error(R.drawable.ic_male)
                    .into(holder.imgCourse);
        }
        }*/


    }


    public int getItemCount() {
        return data.size();
    }

    public void filter(@NonNull String strSearch) {

        //final  List<Course> original = this.originalData;

        if (strSearch.length() == 0) {

            this.data.clear();
            this.data.addAll(this.originalData);

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                List<Lecture> collect = this.originalData.stream()
                        .filter(p -> p.getNamePDF().toLowerCase().contains(strSearch.toLowerCase()))
                        .collect(Collectors.toList());
                this.data.clear();
                data.addAll(collect);
            } else {
                for (Lecture p : originalData) {
                    if (p.getNamePDF().toLowerCase().contains(strSearch.toLowerCase())) {
                        data.add(p);
                    }
                }

            }
        }
        notifyDataSetChanged();
    }

    public class LectureViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate;
        TextView tvNamePDF;
        TextView tvPercent;
        ImageView imgStudent;
        CardView cardView;


        public LectureViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tvDateLecture);
            tvNamePDF = itemView.findViewById(R.id.tvPDFLectureStudent);
            tvPercent = itemView.findViewById(R.id.tvPercentLecture);
        }


    }

}