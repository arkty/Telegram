package org.telegram.ui.Components;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.telegram.tgnet.TLRPC;

public class ReactionView extends FrameLayout {
    private TextView textReaction;

    //BackupImageView photoImage = new BackupImageView(getContext());
    public ReactionView(@NonNull Context context) {
        super(context);
        textReaction = new TextView(context);
        textReaction.setTextSize(24);
        textReaction.setGravity(Gravity.CENTER);
        addView(textReaction, LayoutHelper.createFrame(48, 48, Gravity.CENTER));
    }

    public void setReaction(TLRPC.TL_availableReaction reaction) {
        textReaction.setText(reaction.reaction);

        //TLRPC.Document parentObject = r.select_animation;
//        photoImage.setImage(
//                ImageLocation.getForDocument(parentObject),
//                ImageLoader.AUTOPLAY_FILTER,
//                (Drawable) null, 64,
//                parentObject
//        );
//        photoImage.setBackgroundColor(Color.RED);

        //photoImage.getImageReceiver().setImage(ImageLocation.getForDocument(parentObject), ImageLoader.AUTOPLAY_FILTER, ImageLocation.getForObject(currentPhotoObject, parentObject), null, null, null, null, 0, null, null, 0);
        //photoImage.setDelegate(this);
    }
}
