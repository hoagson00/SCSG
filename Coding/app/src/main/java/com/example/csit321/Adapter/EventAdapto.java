package com.example.csit321.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.csit321.Format.DateFormat;
import com.example.csit321.R;
import com.example.csit321.EntityClass.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventAdapto extends RecyclerView.Adapter<EventAdapto.EventViewHolder> {

    private List <Event> events;
    private Context context;
    private IClickItemEvent iClickItemEvent;

    public interface IClickItemEvent{
        void showEvent(String eventId);
    }

    public EventAdapto (List <Event> events, Context context, IClickItemEvent iClickItemEvent)
    {
        this.events = events;
        this.context = context;
        this.iClickItemEvent = iClickItemEvent;
    }

    public void setEventList (List<Event> eventList)
    {
        this.events = eventList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventView = inflater.inflate(R.layout.event_page_item, parent, false);

        EventViewHolder viewHolder = new EventViewHolder(eventView);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        final Event event = events.get(position);
        if (event == null)
            return;

        DateFormat dateFormat = new DateFormat();

        holder.name.setText(event.getName());
        holder.location.setText("Location: "+event.getLocation());
        try {
            holder.time.setText("From "+dateFormat.dateToString2(event.getStartDate()) +"\n"+"To:  "+dateFormat.dateToString2(event.getEndDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.viewCount.setText(event.getViewCount()+" Views");
        holder.subCount.setText(event.getSubCount()+" Subscribes");
        holder.bind(event.getEventId(),iClickItemEvent);

        if (event.getImageId() != null) {
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("Images").document(event.getImageId());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String url = document.getString("url");
//                            Picasso.with(getApplicationContext()).load(path).into(imageView);
                            Glide.with(holder.image.getContext()).load(url).into(holder.image);
                        }
                    } else {
                        Toast.makeText(holder.image.getContext(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            holder.image.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private TextView name;
        private TextView location;
        private TextView time;
        private TextView viewCount;
        private TextView subCount;
        private ImageView image;

        public EventViewHolder (View itemView)
        {
            super(itemView);
            this.itemView = itemView;
            name = itemView.findViewById(R.id.event_page_textView);
            location = itemView.findViewById((R.id.event_page_textView1));
            time = itemView.findViewById(R.id.event_page_textView2);
            viewCount = itemView.findViewById(R.id.event_page_textView4);
            subCount = itemView.findViewById(R.id.event_page_textView3);
            image = itemView.findViewById(R.id.event_page_item_logo1);
        }

        public void bind (final String eventId, final EventAdapto.IClickItemEvent iClickItemEvent)
        {
            itemView.setOnClickListener (new View.OnClickListener(){
                @Override
                public void onClick (View v)
                {
                    iClickItemEvent.showEvent(eventId);
                }
            });
        }
    }

}
