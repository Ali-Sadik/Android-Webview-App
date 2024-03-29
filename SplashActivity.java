package com.siliconx.studymaterials;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Find the VideoView in your layout
        VideoView videoView = findViewById(R.id.videoView);

        // Set the path of the video file
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.spl;

        // Set the URI of the video file
        Uri uri = Uri.parse(videoPath);

        // Set the URI of the video file to the VideoView
        videoView.setVideoURI(uri);

        // Set a listener to detect when the video finishes preparing
        videoView.setOnPreparedListener(mp -> {
            // Once the video is prepared, get its aspect ratio
            float videoAspectRatio = (float) mp.getVideoWidth() / mp.getVideoHeight();

            // Get the aspect ratio of the device's screen
            float screenAspectRatio = (float) videoView.getWidth() / videoView.getHeight();

            // Adjust the layout params of the VideoView based on aspect ratio
            if (videoAspectRatio > screenAspectRatio) {
                // Video is wider than screen, adjust width
                int newWidth = (int) (videoView.getHeight() * videoAspectRatio);
                videoView.getLayoutParams().width = newWidth;
            } else {
                // Video is taller than screen, adjust height
                int newHeight = (int) (videoView.getWidth() / videoAspectRatio);
                videoView.getLayoutParams().height = newHeight;
            }

            // Start playing the video
            videoView.start();
        });

        // Set a listener to detect when the video finishes playing
        videoView.setOnCompletionListener(mp -> {
            // Once the video finishes, start the MainActivity
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finish SplashActivity to prevent coming back to it
        });
    }
}
