package cesi.com.tchatapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cesi.com.tchatapp.R;
import cesi.com.tchatapp.model.User;

/**
 * The type User adapter.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> users;
    private Context context;

    /**
     * Instantiates a new User adapter.
     *
     * @param ctx the ctx
     */
    public UserAdapter(Context ctx){
        this.context = ctx;
    }

    /**
     * Set user.
     *
     * @param users the users
     */
    public void setUser(List<User> users){
        this.users = users;
        notifyDataSetChanged();
    }


    /**
     * On create view holder user adapter . view holder.
     *
     * @param parent   the parent
     * @param viewType the view type
     * @return the user adapter . view holder
     */
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View convertView = inflater.inflate(R.layout.item_user, parent, false);
        return new ViewHolder(convertView);
    }

    /**
     * On bind view holder.
     *
     * @param vh       the vh
     * @param position the position
     */
    @Override
    public void onBindViewHolder(UserAdapter.ViewHolder vh, int position) {
        vh.username.setText(users.get(position).getUsername());
    }

    /**
     * Gets item id.
     *
     * @param position the position
     * @return the item id
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Gets item count.
     *
     * @return the item count
     */
    @Override
    public int getItemCount() {
        if(users == null){
            return 0;
        }
        return users.size();
    }

    /**
     * The type View holder.
     */
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView username;

        /**
         * Instantiates a new View holder.
         *
         * @param itemView the item view
         */
        ViewHolder(final View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.user_name);
        }
    }
}
