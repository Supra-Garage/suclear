package tw.supra.suclear;

import android.content.Context;

import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * Created by supra on 17-7-24.
 */
@GlideModule
public final class SupraAppGlideModule extends AppGlideModule {
    @Override
    public void registerComponents(Context context, Registry registry) {
        super.registerComponents(context, registry);
    }
}
