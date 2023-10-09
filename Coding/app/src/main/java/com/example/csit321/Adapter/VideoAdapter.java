package com.example.csit321.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.csit321.EntityClass.Post;
import com.example.csit321.EntityClass.Video;
import com.example.csit321.R;
import com.example.csit321.ui.login.Video.UpdateVideo;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private static final String TAG = "VideoAdapter";
    private static final String MyPreferences = "MyPrefs";

    private List<Video> videos;

    private Context mContext;

    public VideoAdapter (List<Video> videos, Context mContext)
    {
        this.videos = videos;
        this.mContext = mContext;
    }

    public void setVideoList (List <Video> videos)
    {
        this.videos = videos;
        notifyDataSetChanged();
    }

    @Override
    public VideoAdapter.VideoViewHolder onCreateViewHolder (ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.video_page_item, parent, false);

        VideoAdapter.VideoViewHolder viewHolder = new VideoAdapter.VideoViewHolder(postView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder (VideoAdapter.VideoViewHolder holder, int pos)
    {
        final Video video = videos.get(pos);
        if (video == null)
        {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        holder.title.setText(video.getTitle());
        holder.description.setText(video.getDescription());
        holder.datePosted.setText("Admin posted in: "+"\n"+sdf.format(video.getDatePosted()));
        holder.viewCounted.setText(video.getView()+" Views");
        holder.goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = video.getUrl();
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://"+url;
                Uri uri = Uri.parse(url);
                mContext.startActivity(new Intent(Intent.ACTION_VIEW,uri));
                updateView(video.getVideoId(), video.getView());
            }
        });

        SharedPreferences sh = mContext.getSharedPreferences(MyPreferences, MODE_PRIVATE);
        String accountType = sh.getString("accountType", "");

        holder.updateButton.setVisibility(View.INVISIBLE);
        if (accountType.equals("admin"))
        {
            holder.updateButton.setVisibility(View.VISIBLE);
            holder.updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext.getApplicationContext(), UpdateVideo.class);
                    intent.putExtra("videoId", video.getVideoId());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    private void updateView(String videoId, int viewCount)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Videos").document(videoId).update("total_views", (viewCount+1));
    }

    public Video findVideo (VideoAdapter.VideoViewHolder holder, int pos)
    {
        return videos.get(pos);
    }

    @Override
    public int getItemCount()
    {
        return videos.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView description;
        private TextView datePosted;
        private TextView viewCounted;
        private Button goButton;
        private Button updateButton;

        public VideoViewHolder (View itemView)
        {
            super (itemView);
            title = itemView.findViewById(R.id.video_page_item_textView);
            description = itemView.findViewById(R.id.video_page_item_textView1);
            datePosted = itemView.findViewById(R.id.video_page_item_textView2);
            viewCounted = itemView.findViewById(R.id.video_page_item_textView4);
            goButton = itemView.findViewById(R.id.video_page_item_button);
            updateButton = itemView.findViewById(R.id.video_page_item_button1);
        }

    }
}
