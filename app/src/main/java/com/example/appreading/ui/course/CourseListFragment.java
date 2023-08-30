package com.example.appreading.ui.course;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appreading.MenuActivity;
import com.example.appreading.R;
import com.example.appreading.adapters.CourseAdapter;
import com.example.appreading.models.Course;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseListFragment extends Fragment implements  SearchView.OnQueryTextListener {

    Button btnAddPatient;
    Button btnViewAll;
    FloatingActionButton favNewPatient;
    FragmentTransaction transaction;
    Fragment patientAddFragment;

    CardView cardViewPatient;

    private int id_carer;
/*    private Carer carer;*/

    String cadenaRespuesta;
/*
    Carer carerLogin = new Carer();*/

    private RecyclerView recyclerView;
    private SearchView svSearchPatient;
    private CourseAdapter patientAdapter;

    private ProgressBar progressBar;
    private int counter = 0;

    ShimmerFrameLayout shimmerFrameLayout;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    public CourseListFragment() {
        super(R.layout.fragment_course_list);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        deleteCache(getContext());
        if (getArguments() != null) {
            id_carer = getArguments().getInt("id_carer", 0);
/*            carer = getArguments().getParcelable("c");*/
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        recyclerView = view.findViewById(R.id.reciclerviewPatient);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));

        patientAdapter = new CourseAdapter();
        recyclerView.setAdapter(patientAdapter);



        svSearchPatient = view.findViewById(R.id.svSearchPatient);

        //Llama a un metodo del activity que toma el carer que inicio sesion
/*        ((MenuActivity)getActivity()).loadData();

        carerLogin = ((MenuActivity)getActivity()).loadData();*/

//       FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//       FragmentTransaction transaction =fragmentManager.beginTransaction();
//       transaction.setReorderingAllowed(true);

        favNewPatient = view.findViewById(R.id.favNewPatient);
        favNewPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
/*                bundle.putSerializable("id_login", carerLogin);*/


                Navigation.findNavController(view).navigate(R.id.action_nav_course_to_courseAddFragment,bundle);


                //  ((MenuActivity)getActivity()).optionSelect();


/*                return;
                Intent intent = new Intent(getContext(), MenuActivity.class);
                startActivity(intent);*/

            }
        });


//        progressBar = view.findViewById(R.id.progressBarLoadingPatient);
//        progressBar.setVisibility(View.VISIBLE);
//
//        Timer timer = new Timer();
//        TimerTask timerTask = new TimerTask() {
//            @SneakyThrows
//            @Override
//            public void run() {
//                counter++;
//
//                progressBar.setProgress(counter);
//
//                if (counter == 100){
//                    timer.cancel();
//                }
//
//            }
//        };
//        timer.schedule(timerTask, 100, 5);

        shimmerFrameLayout = view.findViewById(R.id.shiper_view);
        shimmerFrameLayout.startShimmerAnimation();

        Handler handler = new Handler();
        handler.postDelayed(()->{
            shimmerFrameLayout.stopShimmerAnimation();
            shimmerFrameLayout.setVisibility(View.GONE);
        }, 5000);




        try {
            getCourseListFromFirestore();
/*
            getpatient();



            getUltimoId();
*/


        } catch (Exception e) {
            e.printStackTrace();
        }

        initListener();

    }

    private void getCourseListFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("course")
                .whereEqualTo("teacher_reference", "teacher/" + currentUser.getUid()) // Filtrar cursos del usuario actual
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Course> courseList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Course course = document.toObject(Course.class);
                                courseList.add(course);
                            }
                            // Actualizar el adapter con la lista de cursos obtenida
                            patientAdapter.setData(courseList);
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
        svSearchPatient.setOnQueryTextListener(this);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        patientAdapter.filter(newText);
        return false;
    }
}