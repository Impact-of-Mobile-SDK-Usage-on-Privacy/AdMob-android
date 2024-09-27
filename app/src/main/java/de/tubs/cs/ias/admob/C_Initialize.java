package de.tubs.cs.ias.admob;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import de.tubs.cs.ias.admob.databinding.InitializeBinding;
import kotlin.OptionalExpectation;

public class C_Initialize extends Fragment {

    private InitializeBinding binding;
    private CharSequence text = "4. Step: Initializing";

    private static boolean INITIALIZE_SDK = true;

    private MainActivity getMainActivity() {
        return (MainActivity) this.getActivity();
    }

    private void initializeAdMobSDK() {
        if (INITIALIZE_SDK) {
            MobileAds.initialize(getMainActivity(), new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                    binding.textviewInitialize.setText(text + " [SUCCESS]");
                }
            });
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = InitializeBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(C_Initialize.this)
                        .navigate(R.id.action_Initialize_to_CoreFunctionality);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeAdMobSDK();
    }
}