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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appreading.R;
import com.example.appreading.models.Course;
import com.example.appreading.utils.MoreUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder>  {


    private List<Course> data = new ArrayList<>();
    private  List<Course> originalData = new ArrayList<>();
    private Context context;


    public CourseAdapter() {
    }

    public void setData(List<Course> data) {
        this.data = data;
        this.originalData.addAll(data);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new CourseViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview_course, parent, false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course Course = data.get(position);

        holder.txtname.setText(MoreUtils.coalesce(Course.getName(), "N/D"));
        holder.txtgender.setText(MoreUtils.coalesce(Course.getCode(), "N/D"));
        holder.txtbirthDate.setText(MoreUtils.coalesce(Course.getId(), "N/D"));


//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Navigation.findNavController(view).navigate(R.id.CourseMenuFragment);
//
////                Intent intent = new Intent(context, EvaluadosActivity.class);
////                Bundle b = new Bundle();
////                b.putString("Evaluador", new Gson().toJson(data.get(holder.getAdapterPosition())));
////                intent.putExtras(b);
////                context.startActivity(intent);
//                //Toast.makeText(context, evaluador.getNombres(), Toast.LENGTH_SHORT).show();
//            }
//        };
//        holder.imgEvaluador.setOnClickListener(listener);




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putSerializable("Course",new Gson().toJson(data.get(holder.getAdapterPosition())));

                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
               /* Navigation.findNavController(view).navigate(R.id.CourseMenuFragment,b);*/
            }
        });

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

    public void filter(@NonNull String strSearch ){

        //final  List<Course> original = this.originalData;

        if (strSearch.length() == 0){

            this.data.clear();
            this.data.addAll(this.originalData);

        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                List<Course> collect = this.originalData.stream()
                        .filter(p -> p.getName().toLowerCase().contains(strSearch.toLowerCase()))
                        .collect(Collectors.toList());
                this.data.clear();
                data.addAll(collect);
            }
            else {
                for (Course p: originalData){
                    if (p.getName().toLowerCase().contains(strSearch.toLowerCase())){
                        data.add(p);
                    }
                }

            }
        }
        notifyDataSetChanged();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {

        TextView txtname;
        TextView txtgender;
        TextView txtbirthDate;
        ImageView imgCourse;
        CardView cardView;


        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);

            txtname = itemView.findViewById(R.id.tvNameCarer);
            txtgender = itemView.findViewById(R.id.tvGenderPatient);
            txtbirthDate = itemView.findViewById(R.id.tvbirthDatePatient);
        }


    }
}