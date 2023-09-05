package com.example.appreading.ui.course;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.appreading.R;
import com.example.appreading.adapters.LectureAdapter;
import com.example.appreading.adapters.StudentAdapter;
import com.example.appreading.models.Course;
import com.example.appreading.models.Lecture;
import com.example.appreading.models.Student;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class LectureListFragment extends Fragment  implements  SearchView.OnQueryTextListener {




    Button btnViewAll;

    FragmentTransaction transaction;
    Fragment patientAddFragment;

    CardView cardViewPatient;

    private int id_carer;
    /*    private Carer carer;*/

    String cadenaRespuesta;
/*
    Carer carerLogin = new Carer();*/

    private RecyclerView recyclerView;
    private SearchView svSearchLecture;
    private LectureAdapter lectureAdapter;

    private ProgressBar progressBar;
    private int counter = 0;

    ShimmerFrameLayout shimmerFrameLayout;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    String courseSelectGson;
    Student studentSelect;
    TextView studentFullName;
    public LectureListFragment() {
        super(R.layout.fragment_lecture_list);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        if (getArguments() != null) {

            try {
                studentSelect = (Student) getArguments().getSerializable("studentSelect");
            }catch (Exception e){
                System.out.println("Esto pasa: " + e);
            }

            studentFullName = view.findViewById(R.id.tvStudentLectureTitle);

            studentFullName.setText("Lecturas del Estudiante: "+studentSelect.getName() +" " + studentSelect.getLastName());

/*            courseSelectGson =  getArguments().getString("courseSelect");

            courseSelect =  new Gson().fromJson(courseSelectGson,Course.class);*/

        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        recyclerView = view.findViewById(R.id.recyclerviewLecture);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));

        lectureAdapter = new LectureAdapter();
        recyclerView.setAdapter(lectureAdapter);



        svSearchLecture = view.findViewById(R.id.svSearchLecture);



        shimmerFrameLayout = view.findViewById(R.id.shiper_view);
        shimmerFrameLayout.startShimmerAnimation();

        Handler handler = new Handler();
        handler.postDelayed(()->{
            shimmerFrameLayout.stopShimmerAnimation();
            shimmerFrameLayout.setVisibility(View.GONE);
        }, 3000);




        try {
            getLectureListFromFirestore();
/*
            getpatient();



            getUltimoId();
*/


        } catch (Exception e) {
            e.printStackTrace();
        }

        initListener();

    }

    private void getLectureListFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("lecture")
                .whereEqualTo("id_student", studentSelect.getId()) // Filtrar estudiantes del curso actual
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Lecture> lectureList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Lecture lecture = document.toObject(Lecture.class);
                                lectureList.add(lecture);
                            }
                            // Actualizar el adapter con la lista de cursos obtenida
                            lectureAdapter.setData(lectureList);
                        } else {
                            Log.e("FirestoreError", "Error al obtener la lista de cursos: " + task.getException().getMessage());
                        }
                    }
                });
    }

/*    public void getpatient() throws Exception {

        String id = ""+carerLogin.getId();
        Call<List<Patient>> patientList = Apis.getPatientService().getPatient(id);

        patientList.enqueue(new Callback<List<Patient>>() {
            @Override
            public void onResponse(Call<List<Patient>> call, Response<List<Patient>> response) {
                if(response.isSuccessful()){
                    List <Patient>  patients = response.body();
                    patientAdapter.setData(patients);
                }
            }

            @Override
            public void onFailure(Call<List<Patient>> call, Throwable t) {
                Log.e("faliure", t.getLocalizedMessage());
            }
        });
    }

    public String getUltimoId(){
        Call<String> ultimo = Apis.getMedicalTreatmentService().getMedicalTreatmentLastId();
        ultimo.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    String respuesta = response.body();
                    cadenaRespuesta = respuesta;
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
        return "hola";
    }*/

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


    private  void initListener(){
        svSearchLecture.setOnQueryTextListener(this);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        lectureAdapter.filter(newText);
        return false;
    }
}