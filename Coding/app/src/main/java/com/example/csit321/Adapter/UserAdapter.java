package com.example.csit321.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import com.example.csit321.R;
import com.example.csit321.EntityClass.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    private List <User> users;

    private Context mContext;
    private IClickItemUser iClickItemUser;

    public interface IClickItemUser{
        void showDetail (String userId, String fullName);
    }

    public UserAdapter (List<User> user, Context mContext, UserAdapter.IClickItemUser iClickItemUser)
    {
        this.users = user;
        this.mContext = mContext;
        this.iClickItemUser = iClickItemUser;
    }

    public void setUserList (List<User> userList)
    {
        this.users = userList;
        notifyDataSetChanged();
    }

    @Override
    public UserViewHolder onCreateViewHolder (ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View userView = inflater.inflate(R.layout.user_item, parent, false);

        UserViewHolder viewHolder = new UserViewHolder(userView);
        return viewHolder;
//        return new UserViewHolder(userView).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int pos) {
        final User user = users.get(pos);
        if (user == null){
            return;
        }

        holder.name.setText(user.getFullName());
        holder.email.setText(user.getUsername());
        holder.purpose.setText(user.getAccountType());
        holder.bind(user.getUserId(), user.getFullName(),iClickItemUser);
    }

    public User findUser (UserViewHolder holder, int position){
        return users.get(position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{

        private View itemview;
        private TextView name;
        private TextView email;
        private TextView purpose;

        public UserViewHolder(View itemView)
        {
            super(itemView);
            itemview = itemView;
            name = itemView.findViewById(R.id.user_item_view);
            email = itemView.findViewById(R.id.user_item_view1);
            purpose = itemView.findViewById(R.id.user_item_view2);
        }

        public void bind (final String userId, final String fullName , final UserAdapter.IClickItemUser iClickItemUser)
        {
            itemView.setOnClickListener (new View.OnClickListener(){
                @Override
                public void onClick (View v)
                {
                    iClickItemUser.showDetail(userId,fullName);
                }
            });
        }

    }

}
