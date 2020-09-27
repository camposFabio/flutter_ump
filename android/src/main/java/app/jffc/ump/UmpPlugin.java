package app.jffc.ump;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

// Consent imports
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;


/** UmpPlugin */
public class UmpPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Context context;
  private Activity activity;

  private ConsentInformation consentInformation;
  private ConsentForm consentForm;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(),
        "app.jffc/ump");
    context = flutterPluginBinding.getApplicationContext();
    channel.setMethodCallHandler(this);
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel =
        new MethodChannel(registrar.messenger(), "app.jffc/ump");

    channel.setMethodCallHandler(new UmpPlugin());
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

    switch (call.method) {
      case "getUserConsent":
        final String testDeviceID = call.argument("testDeviceID");


        result.success(getUserConsent(testDeviceID));
        break;
      case "resetUserConsent":
        resetUserConsent();
        break;
      case "getConsentStatus":
        result.success(getConsentStatus());
        break;
      default:
        result.notImplemented();
    }

  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  // Activity
  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    // Your plugin is now associated with an Android Activity.
    //
    // If this method is invoked, it is always invoked after
    // onAttachedToFlutterEngine().
    //
    // You can obtain an Activity reference with
    // binding.getActivity()
    //
    // You can listen for Lifecycle changes with
    // binding.getLifecycle()
    //
    // You can listen for Activity results, new Intents, user
    // leave hints, and state saving callbacks by using the
    // appropriate methods on the binding.

    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    // The Activity your plugin was associated with has been
    // destroyed due to config changes. It will be right back
    // but your plugin must clean up any references to that
    // Activity and associated resources.
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
    // Your plugin is now associated with a new Activity instance
    // after config changes took place. You may now re-establish
    // a reference to the Activity and associated resources.
  }

  @Override
  public void onDetachedFromActivity() {
    // Your plugin is no longer associated with an Activity.
    // You must clean up all resources and references. Your
    // plugin may, or may not ever be associated with an Activity
    // again.
  }



  protected int getUserConsent(String testDeviceID) {

    ConsentRequestParameters params;

    // If receive an test Device ID
    // Force Geography for testing


    if (testDeviceID != null && !testDeviceID.isEmpty()) {
      System.out.println("***** Java getUserConsent");
      System.out.println("***** Java testDeviceID: "+ testDeviceID);
      
      ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(context)
          .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
          .addTestDeviceHashedId(testDeviceID).build();

      params =
          new ConsentRequestParameters.Builder().setConsentDebugSettings(debugSettings).build();


    } else {

      params = new ConsentRequestParameters.Builder().build();
    }


    consentInformation = UserMessagingPlatform.getConsentInformation(context);

    consentInformation.requestConsentInfoUpdate(activity, params,
        new ConsentInformation.OnConsentInfoUpdateSuccessListener() {

          @Override
          public void onConsentInfoUpdateSuccess() {

            // The consent information state was updated.
            // You are now ready to check if a form is available.
            if (consentInformation.isConsentFormAvailable()) {

              loadForm();
            }
          }
        }, new ConsentInformation.OnConsentInfoUpdateFailureListener() {
          @Override
          public void onConsentInfoUpdateFailure(FormError formError) {
            // Handle the error.
          }
        });
        return getConsentStatus();
  }

  protected void resetUserConsent() {
    
    consentInformation = UserMessagingPlatform.getConsentInformation(context);

    // Reset consent state
    consentInformation.reset();

  }

  protected int getConsentStatus() {
    

    consentInformation = UserMessagingPlatform.getConsentInformation(context);

    return (consentInformation.getConsentStatus());


  }



  protected void loadForm() {


    UserMessagingPlatform.loadConsentForm(context,
        new UserMessagingPlatform.OnConsentFormLoadSuccessListener() {
          @Override
          public void onConsentFormLoadSuccess(ConsentForm consentForm) {


            UmpPlugin.this.consentForm = consentForm;
            if (consentInformation
                .getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {

              consentForm.show(activity, new ConsentForm.OnConsentFormDismissedListener() {
                @Override
                public void onConsentFormDismissed(@Nullable FormError formError) {
                  // Handle dismissal by reloading form.
                  loadForm();
                }
              });

            }

          }
        }, new UserMessagingPlatform.OnConsentFormLoadFailureListener() {
          @Override
          public void onConsentFormLoadFailure(FormError formError) {
            /// Handle Error.
          }
        });
  }

}
