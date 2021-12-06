package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.util.Log;
import android.util.StateSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;

import java.util.ArrayList;
import java.util.List;

public class MessageReactionsLayout extends FrameLayout {
    private FlexboxLayout flex;
    private ChatMessageCell.ChatMessageCellDelegate delegate;
    private MessageObject message;
    private TLRPC.TL_messageReactions reactions;

    // 159x78
    private int itemWidth = AndroidUtilities.dp(53);
    private int itemHeight = AndroidUtilities.dp(28);
    // colors -> 5BA756 border, inside -> 10% opacity
    // 53 x 26

    private Theme.ResourcesProvider resourcesProvider;

    public MessageReactionsLayout(@NonNull Context context, Theme.ResourcesProvider resourcesProvider, ChatMessageCell.ChatMessageCellDelegate delegate, MessageObject message) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        flex = new FlexboxLayout(context);
        this.delegate = delegate;
        this.message = message;
        flex.setFlexDirection(FlexDirection.ROW);
        flex.setFlexWrap(FlexWrap.WRAP);
        flex.setJustifyContent(JustifyContent.FLEX_START);
        flex.setAlignItems(AlignItems.FLEX_START);
        addView(flex, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    public int calculateHeight(int width) {
        int itemsOnRow = width / (itemWidth + AndroidUtilities.dp(8));
        if(itemsOnRow == 0) {
            return 0;
        }
        int rows = flex.getChildCount() / itemsOnRow + ((flex.getChildCount() % itemsOnRow > 0) ? 1 : 0);
        //int d = flex.getChildCount() / itemsOnRow;
        //int r = flex.getChildCount() % itemsOnRow;
        return rows * (itemHeight + AndroidUtilities.dp(8));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return flex.dispatchTouchEvent(ev);
    }

    public void setReactions(TLRPC.TL_messageReactions reactions) {
        this.reactions = reactions;
        flex.removeAllViews();
        for (TLRPC.TL_reactionCount r : reactions.results) {

            TextView t = new TextView(getContext());

            GradientDrawable bg;
            if(r.chosen) {
                bg = (GradientDrawable) getContext().getResources().getDrawable(R.drawable.bg_reaction_chosen);
            } else {
                bg = (GradientDrawable) getContext().getResources().getDrawable(R.drawable.bg_reaction);
            }
            bg.setColorFilter(getThemedColor(Theme.key_chat_inReplyNameText), PorterDuff.Mode.MULTIPLY);
            t.setBackground(bg);
            t.setTextSize(12);
            t.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            t.setTextColor(getThemedColor(Theme.key_chat_inReplyNameText));
            t.setGravity(Gravity.CENTER);
            t.setClickable(true);
            t.setOnTouchListener((v, ev) -> {
                Log.v("Reactions_2", "onTouch: " + ev);
                if(ev.getAction() == MotionEvent.ACTION_UP) {
                    Log.v("Reactions_2", "onSetReaction: " + r.chosen + "," + r.reaction);
                    if(r.chosen) {
                        delegate.didSetReaction(message, null);
                    } else {
                        delegate.didSetReaction(message, r.reaction);
                    }
                    return true;
                }
                return false;
            });
            t.setText(r.reaction + " " + LocaleController.formatShortNumber(r.count, null));

            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    itemWidth,
                    itemHeight
            );
            params.setMargins(AndroidUtilities.dp(4), 0, 0, AndroidUtilities.dp(4));
            flex.addView(t, params);
        }
        flex.addView(new View(getContext()), new FlexboxLayout.LayoutParams(itemWidth * 2, itemHeight));
    }

    public void setDummyReactions() {
        flex.removeAllViews();
        List<String> r = new ArrayList<>();
        r.add("\uD83C\uDF84");
        r.add("\uD83D\uDE0A");
        r.add("\uD83D\uDE49");
        r.add("\uD83D\uDD25");
        r.add("\uD83C\uDF81");
        r.add("\uD83C\uDF84");
        r.add("\uD83D\uDE0A");
        r.add("\uD83D\uDE49");
        r.add("\uD83D\uDD25");
        r.add("\uD83C\uDF81");
        for (int i = 0; i < r.size(); i++) {
            TextView t = new TextView(getContext());
            GradientDrawable bg = (GradientDrawable) getContext().getResources().getDrawable(R.drawable.bg_reaction);
            bg.setColorFilter(getThemedColor(Theme.key_chat_inReplyNameText), PorterDuff.Mode.MULTIPLY);
            t.setBackground(bg);
            t.setTextSize(12);
            t.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            t.setTextColor(getThemedColor(Theme.key_chat_inReplyNameText));
            t.setGravity(Gravity.CENTER);
            t.setText(r.get(i) + " " + LocaleController.formatShortNumber((i + 1) * (int) Math.pow(10, i), null));
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    itemWidth,
                    itemHeight
            );
            params.setMargins(AndroidUtilities.dp(4), 0, 0, AndroidUtilities.dp(4));
            flex.addView(t, params);
        }
    }
    private int getThemedColor(String key) {
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color : Theme.getColor(key);
    }
}
