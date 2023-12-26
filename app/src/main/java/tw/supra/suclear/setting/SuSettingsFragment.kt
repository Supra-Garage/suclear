package tw.supra.suclear.setting

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import tw.supra.suclear.R

class SuSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}