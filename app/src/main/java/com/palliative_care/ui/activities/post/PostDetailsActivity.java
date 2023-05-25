package com.palliative_care.ui.activities.post;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.MediaController;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.palliative_care.R;
import com.palliative_care.base.BaseActivity;
import com.palliative_care.databinding.ActivityPostDetailsBinding;
import com.palliative_care.helpers.Constants;
import com.palliative_care.helpers.Functions;
import com.palliative_care.model.Post;
import com.palliative_care.model.User;
import com.palliative_care.ui.activities.chat.MessagesActivity;
import com.palliative_care.ui.activities.doctor.AddPostActivity;

import java.io.IOException;

public class PostDetailsActivity extends BaseActivity {

    ActivityPostDetailsBinding binding;
    Post model;
    String from = Constants.TYPE_PATIENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        model = (Post) getIntent().getSerializableExtra(Constants.TYPE_MODEL);
        from = Functions.getUser().getType();
        switch (from) {
            case Constants.TYPE_PATIENT:
                binding.message.setText(getString(R.string.message_doctor));
                binding.message.setOnClickListener(v -> {
                    getDoctorInfoRequest();
                });
                break;
            case Constants.TYPE_DOCTOR:
                binding.message.setText(getString(R.string.edit));
                binding.message.setOnClickListener(v -> {
                    Intent intent = new Intent(PostDetailsActivity.this, AddPostActivity.class);
                    intent.putExtra(Constants.TYPE_FROM, Constants.TYPE_EDIT);
                    intent.putExtra(Constants.TYPE_MODEL, model);
                    startActivity(intent);
                });
                break;
        }
        binding.appbar.back.setOnClickListener(v -> finish());
        binding.appbar.title.setText(getString(R.string.post_details));
        String createAt = Functions.formatTime(model.getCreateAt());
        binding.userName.setText(model.getDoctorName());
        binding.time.setText(createAt);
        binding.categoryName.setText(model.getCategoryName());
        binding.titlePost.setText(model.getTitle());
        binding.content.setText(model.getContent());
        initFileType(model.getFileType());
        runnable = this::seekBarUpdate;
    }

    private void getDoctorInfoRequest() {
        showCustomProgress();
        FirebaseDatabase.getInstance().getReference(Constants.TABLE_USERS).child(model.getDoctorId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dismissCustomProgress();
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            addNewContactRequest(user);
                        } else {
                            Functions.snackBar(PostDetailsActivity.this, binding.getRoot(), getString(R.string.error));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dismissCustomProgress();
                        Functions.snackBar(PostDetailsActivity.this, binding.getRoot(), getString(R.string.error));
                    }
                });
    }

    private void addNewContactRequest(User user) {
        FirebaseDatabase.getInstance().getReference(Constants.TABLE_CONTACTS)
                .child(model.getDoctorId()).child(Functions.getUserId())
                .setValue(Functions.getUser());

        FirebaseDatabase.getInstance().getReference(Constants.TABLE_CONTACTS)
                .child(Functions.getUserId())
                .child(model.getDoctorId())
                .setValue(user).addOnCompleteListener(task -> {
                    dismissCustomProgress();
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(this, MessagesActivity.class);
                        intent.putExtra(Constants.TYPE_MODEL, user);
                        startActivity(intent);
                    } else {
                        Functions.snackBar(this, binding.getRoot(), getString(R.string.error));
                    }
                });
    }

    private boolean isPlaying = false;
    private int progressValue = 0;
    private MediaPlayer player;
    private Runnable runnable;

    private void initFileType(String type) {
        switch (type) {
            case "image":
                binding.postImage.setVisibility(View.VISIBLE);
                Glide.with(this).load(model.getFile()).into(binding.postImage);
                break;
            case "video":
                binding.postVideo.setVisibility(View.VISIBLE);
                MediaController mediaController = new MediaController(this);
                binding.postVideo.setMediaController(mediaController);
                binding.postVideo.setVideoURI(Uri.parse(model.getFile()));
                binding.postVideo.requestFocus();
                binding.postVideo.setOnPreparedListener(mp -> {
                    binding.postVideo.start();
                });
                break;
            case "audio":
                binding.postAudioContainer.setVisibility(View.VISIBLE);
                binding.play.setOnClickListener(v -> {
                    if (!isPlaying) {
                        isPlaying = true;
                        startPlaying();
                    } else {
                        isPlaying = false;
                        stopPlaying();
                    }
                });
                break;
            case "file":
                binding.postFileContainer.setVisibility(View.VISIBLE);
                binding.fileName.setText(model.getFile());
                binding.postFileContainer.setOnClickListener(v -> {
                });
                break;
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(model.getFile());
            player.prepare();
            player.setOnPreparedListener(mp -> {
                player.start();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        binding.play.setImageResource(R.drawable.ic_stop);
        binding.seekbar.setProgress(progressValue);
        player.seekTo(progressValue);
        binding.seekbar.setMax(player.getDuration());
        seekBarUpdate();
        player.setOnCompletionListener(mp -> {
            binding.play.setImageResource(R.drawable.ic_play);
            isPlaying = false;
            player.seekTo(0);
        });
        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (player != null && fromUser) {
                    player.seekTo(progress);
                    progressValue = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void stopPlaying() {
        try {
            player.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        player = null;
        binding.play.setImageResource(R.drawable.ic_play);
    }

    private void seekBarUpdate() {
        if (player != null) {
            int pos = player.getCurrentPosition();
            binding.seekbar.setProgress(pos);
            progressValue = pos;
        }
        new Handler(Looper.myLooper()).postDelayed(runnable, 100);
    }

}