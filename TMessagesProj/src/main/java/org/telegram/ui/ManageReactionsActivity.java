package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ManageReactionsActivity extends BaseFragment {

    private TextCheckCell enableCell;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;

    private final long chatId;
    private final TLRPC.ChatFull chatFull;
    private final TLRPC.TL_messages_availableReactions availableReactions;
    private final ArrayList<String> enabledReactions;

    private boolean reactionsEnabled;

    public ManageReactionsActivity(long chatId, boolean reactionsEnabled) {
        super();
        this.chatId = chatId;
        this.availableReactions = MessagesController.getInstance(currentAccount).availableReactions;
        this.chatFull = MessagesController.getInstance(currentAccount).getChatFull(chatId);
        this.enabledReactions = new ArrayList<>(chatFull.available_reactions);
        this.reactionsEnabled = reactionsEnabled;
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("Reactions", R.string.Reactions));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        fragmentView = new FrameLayout(context);
        FrameLayout root = (FrameLayout) fragmentView;
        enableCell = new TextCheckCell(context);
        enableCell.setTextAndCheck("Enable Reactions", false, true);
        enableCell.setChecked(reactionsEnabled);

        enableCell.setOnClickListener((v) -> {
            reactionsEnabled = !reactionsEnabled;
            enableCell.setChecked(reactionsEnabled);
            if (!reactionsEnabled) {
                getMessagesController().setChatAvailableReactions(chatId, new ArrayList<>());
            } else {
                enabledReactions.clear();
                enabledReactions.addAll(availableReactions.reactions.stream().map((it) -> it.reaction).collect(Collectors.toList()));
                listViewAdapter.notifyDataSetChanged();
                getMessagesController().setChatAvailableReactions(chatId, enabledReactions);
            }
            updateReactionsList();
        });
        root.addView(enableCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(listViewAdapter = new ListAdapter(context));
        listView.setVerticalScrollbarPosition(LocaleController.isRTL ? RecyclerListView.SCROLLBAR_POSITION_LEFT : RecyclerListView.SCROLLBAR_POSITION_RIGHT);

        listView.setOnItemClickListener((view, position) -> {
            TLRPC.TL_availableReaction reaction = availableReactions.reactions.get(position);
            TextCheckCell cell = (TextCheckCell) view;
            if(enabledReactions.contains(reaction.reaction)) {
                enabledReactions.remove(reaction.reaction);
                cell.setChecked(false);
            } else {
                enabledReactions.add(reaction.reaction);
                cell.setChecked(true);
            }
            getMessagesController().setChatAvailableReactions(chatId, enabledReactions);
        });
        root.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 0, 0, 64, 0, 0));

        updateReactionsList();
        return fragmentView;
    }

    private void updateReactionsList() {
        if (reactionsEnabled) {
            listView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.GONE);
        }
    }

    private class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;

        public ListAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextCheckCell cell = new TextCheckCell(context);
            return new RecyclerListView.Holder(cell);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TLRPC.TL_availableReaction reaction = availableReactions.reactions.get(position);
            TextCheckCell view = ((TextCheckCell) holder.itemView);
            view.setTextAndCheck(reaction.reaction + " " + reaction.title, enabledReactions.contains(reaction.reaction), true);
        }

        @Override
        public int getItemCount() {
            return availableReactions != null ? availableReactions.reactions.size() : 0;
        }
    }
}
