package com.example.appreading.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavGraph;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.appreading.R;
import com.example.appreading.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    Button btnViewPatients;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnViewPatients = root.findViewById(R.id.btn_begin_courses);
        btnViewPatients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  Navigation.findNavController(view).navigate(R.id.nav_patients);
                NavOptions navOptions = new NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setRestoreState(true)
                        .setPopUpTo(NavGraph.findStartDestination(Navigation.findNavController(view).getGraph()).getId(),
                                false, // inclusive
                                true) // saveState
                        .build();
                Navigation.findNavController(view).navigate(R.id.nav_course, null, navOptions);



            }
        });

/*        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}