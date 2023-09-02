package com.example.appreading;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appreading.databinding.ActivityMenuBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMenuBinding binding;


    TextView tvNameUserMenu, tvEmailMenu;
    private ShapeableImageView navHeaderImageView;

    String name ="";
    String email ="";

    String photoUrl ="";
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    int contador = 0;
    int destinoActualId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View vistaHeader = binding.navView.getHeaderView(0);

        tvNameUserMenu = vistaHeader.findViewById(R.id.tv_name_menu);
        tvEmailMenu = vistaHeader.findViewById(R.id.tv_email_menu);

        if (currentUser != null) {
            String uid = currentUser.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("user").document(uid);

            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        name = documentSnapshot.getString("name");
                        String lastName = documentSnapshot.getString("lastName");
                        email = documentSnapshot.getString("email");
                        // ... Obtener otros campos según tu estructura

                        photoUrl = documentSnapshot.getString("photo_url");

                        navHeaderImageView = vistaHeader.findViewById((R.id.imv_user));

                        Glide.with(getApplicationContext())
                                .load(photoUrl)
                                .error(R.drawable.ic_user)
                                .into(navHeaderImageView);

                        // Puedes utilizar esta información según tus necesidades
                        tvNameUserMenu.setText("Nombre: "+ name +" "+ lastName);
                        tvEmailMenu.setText("Email: "+ email );

                    } else {
                        // El documento del usuario no existe en la colección
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Error al obtener el documento
                }
            });
        } else {
            // El usuario no está autenticado
        }





        setSupportActionBar(binding.appBarMenu.toolbar);
        binding.appBarMenu.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_course, R.id.nav_report, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();
                if (id == R.id.nav_logout) {
                    //     navController.navigate(R.id.nav_logout);
                    showLogoutDialog(navController);
                } else if (id == R.id.nav_home) {
                    navController.popBackStack(R.id.nav_home, true);
                    navController.navigate(R.id.nav_home);
                } else {
                    // Make your navController object final above
                    // or call Navigation.findNavController() again here
                    NavigationUI.onNavDestinationSelected(menuItem, navController);
                }
                drawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.nav_home || destination.getId() == R.id.nav_logout) {
                    destinoActualId = R.id.nav_home;
                } else {

                    destinoActualId = 0;
                }
            }
        });


    }

    private boolean logoutDialogShown = false; // variable to track if the logout dialog has been shown
    private void showLogoutDialog(NavController navController) {
        if (!logoutDialogShown) {
            logoutDialogShown = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirmación de cierre de sesión");
            builder.setMessage("¿Estás seguro de que deseas cerrar sesión?");
            builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                @SneakyThrows
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    navController.navigate(R.id.nav_logout);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logoutDialogShown = false;
                    // Perform the desired action when the "No" button is clicked
                }
            });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    logoutDialogShown = false;
                }
            });
            builder.show();
        }
    }
    @Override
    public void onBackPressed() {
        if (destinoActualId == R.id.nav_home) {
            if (contador == 0) {
                Toast.makeText(getApplicationContext(), "Presiona de nuevo para salir", Toast.LENGTH_SHORT).show();
                contador++;
            } else {
                super.onBackPressed();
            }

            new CountDownTimer(3000, 1000) {
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    contador = 0;
                }
            }.start();
        }else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}