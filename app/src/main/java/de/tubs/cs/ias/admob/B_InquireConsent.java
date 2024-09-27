package de.tubs.cs.ias.admob;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;
import de.tubs.cs.ias.admob.databinding.InquireConsentBinding;

public class B_InquireConsent extends Fragment {

    private InquireConsentBinding binding;
    private ConsentForm consentForm = null;
    private ConsentInformation consentInformation;

    private static final boolean INIT_CONSENT = true;
    private static final boolean INQUIRE_CONSENT = true;
    private static final boolean FORCE_GEOGRAPHY_EEA = false;
    private static final boolean CONSENT_UNDER_AGE = false;


    private CharSequence text = "3. State: Consent";

    private MainActivity getMainActivity() {
        return (MainActivity) this.getActivity();
    }

    public boolean initializeConsent() {
        final boolean[] success = {false};
        consentInformation = UserMessagingPlatform.getConsentInformation(this.getMainActivity());
        ConsentRequestParameters params;
        if (FORCE_GEOGRAPHY_EEA) {
            ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(this.getMainActivity())
                    .setDebugGeography(
                            ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                    .addTestDeviceHashedId("TEST-DEVICE-HASHED-ID")
                    .build();
            params = new ConsentRequestParameters
                    .Builder()
                    .setConsentDebugSettings(debugSettings)
                    .setTagForUnderAgeOfConsent(CONSENT_UNDER_AGE)
                    .build();
        } else {
            // this is the amended version based on https://developers.google.com/interactive-media-ads/ump/android/quick-start
            params = new ConsentRequestParameters
                    .Builder()
                    .setTagForUnderAgeOfConsent(CONSENT_UNDER_AGE)
                    .build();
        }
        this.consentInformation.requestConsentInfoUpdate(
                this.getMainActivity(),
                params,
                new ConsentInformation.OnConsentInfoUpdateSuccessListener() {
                    @Override
                    public void onConsentInfoUpdateSuccess() {
                        binding.textviewInquireConsent.setText(B_InquireConsent.this.text + " [UPDATE SUCCESS]");
                        updateTextDetails(1);
                        success[0] = true;
                    }
                },
                new ConsentInformation.OnConsentInfoUpdateFailureListener() {
                    @Override
                    public void onConsentInfoUpdateFailure(@NonNull FormError formError) {
                        binding.textviewInquireConsent.setText(B_InquireConsent.this.text + " [UPDATE FAILURE]");
                        updateTextDetails(2);
                        success[0] = false;
                    }
                });
        return success[0];
    }

    public boolean loadForm() {
        final boolean[] success = {false};
        UserMessagingPlatform.loadConsentForm(
                this.getMainActivity(), new UserMessagingPlatform.OnConsentFormLoadSuccessListener() {
                    @Override
                    public void onConsentFormLoadSuccess(ConsentForm consentForm) {
                        B_InquireConsent.this.consentForm = consentForm;
                        if( B_InquireConsent.this.consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                            consentForm.show(getMainActivity(),
                                    new ConsentForm.OnConsentFormDismissedListener() {
                                        @Override
                                        public void onConsentFormDismissed(@Nullable FormError formError) {
                                            binding.textviewInquireConsent.setText(B_InquireConsent.this.text + " [FORM DISMISSED]");
                                            updateTextDetails(3);
                                            loadForm();
                                        }
                                    });
                            success[0] = true;
                        } else {
                            binding.textviewInquireConsent.setText(B_InquireConsent.this.text + " [NO FURTHER CONSENT REQ.]");
                            updateTextDetails(4);
                        }
                    }
                },
                new UserMessagingPlatform.OnConsentFormLoadFailureListener() {
                    @Override
                    public void onConsentFormLoadFailure(FormError formError) {
                        // Handle the error.
                        binding.textviewInquireConsent.setText(B_InquireConsent.this.text + " [FAILED FORM LOAD]");
                        updateTextDetails(5);
                        success[0] = false;
                    }
                });
        return success[0];
    }

    private void updateTextDetails(int step) {
        if(consentInformation != null) {
            if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.UNKNOWN) {
                binding.textviewInquireConsentState.setText (step + " Consent Status: UNKNOWN // May Display Ads: " + consentInformation.canRequestAds());
            } else if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.NOT_REQUIRED) {
                binding.textviewInquireConsentState.setText (step + " Consent Status: NOT REQUIRED // May Display Ads: " + consentInformation.canRequestAds());
            } else if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                binding.textviewInquireConsentState.setText (step + " Consent Status: REQUIRED // May Display Ads: " + consentInformation.canRequestAds());
            } else if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.OBTAINED) {
                binding.textviewInquireConsentState.setText (step + " Consent Status: OBTAINED // May Display Ads: " + consentInformation.canRequestAds());
            } else {
                String val = "UNKNOWN VALUE '" + consentInformation.getConsentStatus() + "'//" + consentInformation.canRequestAds();
                binding.textviewInquireConsentState.setText (step + " " + val);
            }
        } else {
            binding.textviewInquireConsentState.setText (step + " consentInformation not initialized");
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = InquireConsentBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(B_InquireConsent.this)
                        .navigate(R.id.action_InquireConsent_to_Initialize);
            }
        });

        binding.buttonInquire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(INQUIRE_CONSENT) {
                    loadForm();
                }
            }
        });

        binding.buttonInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(INQUIRE_CONSENT || INIT_CONSENT) {
                    initializeConsent();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
