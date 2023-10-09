package com.example.csit321.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.csit321.EntityClass.Post;
import com.example.csit321.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts;

    private Context mContext;
    private IClickItemPost iClickItemPost;

    public interface IClickItemPost{
        void showDetail(String postId);
    }

    public PostAdapter (List<Post> posts, Context mContext, IClickItemPost iClickItemPost)
    {
        this.posts = posts;
        this.mContext = mContext;
        this.iClickItemPost = iClickItemPost;
    }

    public void setPostList (List <Post> postList)
    {
        this.posts = postList;
        notifyDataSetChanged();
    }

    @Override
    public PostViewHolder onCreateViewHolder (ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.home_page_item, parent, false);

        PostViewHolder viewHolder = new PostViewHolder (postView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder (PostViewHolder holder, int pos)
    {
        final Post post = posts.get(pos);
        if (post == null)
        {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        holder.title.setText(post.getTitle());
        holder.description.setText(post.getDescription());
        holder.datePosted.setText("Admin posted in: "+"\n"+sdf.format(post.getDate()));
        holder.likeCounted.setText(post.getLikedCount()+" Liked");
        holder.viewCounted.setText(post.getViewCount()+" Views");
        holder.bind(post.getPostID(),iClickItemPost);
    }


    public Post findPost (PostViewHolder holder, int pos)
    {
        return posts.get(pos);
    }

    @Override
    public int getItemCount()
    {
        return posts.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        private View itemView;
        private TextView title;
        private TextView description;
        private TextView datePosted;
        private TextView likeCounted;
        private TextView viewCounted;

        public PostViewHolder (View itemView)
        {
            super (itemView);
            this.itemView = itemView;
            title = itemView.findViewById(R.id.home_page_item_textView);
            description = itemView.findViewById(R.id.home_page_item_textView1);
            datePosted = itemView.findViewById(R.id.home_page_item_textView2);
            likeCounted = itemView.findViewById(R.id.home_page_item_textView3);
            viewCounted = itemView.findViewById(R.id.home_page_item_textView4);
        }

        public void bind (final String postId, final IClickItemPost iClickItemPost)
        {
            itemView.setOnClickListener (new View.OnClickListener(){
                @Override
                public void onClick (View v)
                {
                    iClickItemPost.showDetail(postId);
                }
            });
        }
    }

}
