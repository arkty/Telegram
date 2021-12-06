package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReactionsLayout extends FrameLayout {

    private RectF bgRect = new RectF(0,0,0,0);
    private Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<TLRPC.TL_availableReaction> reactions;
    private OnReaction onReaction;

    public ReactionsLayout(ChatActivity fragment, TLRPC.ChatFull chatInfo, MessageObject message, OnReaction onReaction) {
        super(fragment.getParentActivity());
        this.onReaction = onReaction;

        ArrayList<TLRPC.TL_availableReaction> allReaction = fragment.getMessagesController().availableReactions.reactions;
        this.reactions = allReaction.stream().filter((r) -> chatInfo.available_reactions.contains(r.reaction)).collect(Collectors.toList());
        bgPaint.setColor(Color.WHITE);
        bgPaint.setStyle(Paint.Style.FILL);

        HorizontalScrollView scroll = new HorizontalScrollView(getContext());
        scroll.setHorizontalScrollBarEnabled(false);
        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.HORIZONTAL);

        for (TLRPC.TL_availableReaction r : reactions) {
            ReactionView v = new ReactionView(getContext());
            v.setReaction(r);
            v.setOnClickListener((view) -> {
                fragment.getSendMessagesHelper().sendReaction(message, r.reaction);
                this.onReaction.onReactionSelected(r);
            });
            container.addView(v, LayoutHelper.createLinear(48, 48));
        }
        //StickerView
        //container.addView();
        scroll.addView(container, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        addView(scroll);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        bgRect.right = getMeasuredWidth();
        bgRect.bottom = getMeasuredHeight();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRoundRect(bgRect, AndroidUtilities.dp(16), AndroidUtilities.dp(16), bgPaint);
        super.draw(canvas);
    }

    public interface OnReaction {
        void onReactionSelected(TLRPC.TL_availableReaction reaction);
    }
}
