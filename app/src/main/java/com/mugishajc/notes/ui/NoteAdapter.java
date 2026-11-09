package com.mugishajc.notes.ui;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.mugishajc.notes.R;
import com.mugishajc.notes.data.Note;
import com.mugishajc.notes.databinding.ItemNoteBinding;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteViewHolder> {

    public interface Listener {
        void onNoteClicked(Note note);

        void onPinClicked(Note note);
    }

    static final DiffUtil.ItemCallback<Note> DIFF = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.equals(newItem);
        }
    };

    private final Listener listener;

    public NoteAdapter(Listener listener) {
        super(DIFF);
        this.listener = listener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    /** Exposed for the swipe handler, which only knows adapter positions. */
    public Note noteAt(int position) {
        return getItem(position);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoteBinding binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        private final ItemNoteBinding binding;

        NoteViewHolder(ItemNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onNoteClicked(getItem(position));
                }
            });
            binding.pinButton.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onPinClicked(getItem(position));
                }
            });
        }

        void bind(Note note) {
            binding.title.setText(note.getTitle());
            binding.body.setText(note.getBody());
            binding.body.setVisibility(note.getBody().isEmpty() ? View.GONE : View.VISIBLE);
            binding.category.setText(CategoryLabels.of(note.getCategory()));
            binding.updated.setText(DateUtils.getRelativeTimeSpanString(
                    note.getUpdatedAt(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));

            binding.pinButton.setAlpha(note.isPinned() ? 1f : 0.35f);
            binding.pinButton.setContentDescription(binding.getRoot().getContext()
                    .getString(note.isPinned() ? R.string.unpin : R.string.pin));
        }
    }
}
