package cesi.com.tchatapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cesi.com.tchatapp.R;
import cesi.com.tchatapp.helper.DateHelper;
import cesi.com.tchatapp.model.Message;

/**
 * The type Messages adapter.
 */
public class MessagesAdapter extends BaseAdapter {

    private final Context context;

    /**
     * Instantiates a new Messages adapter.
     *
     * @param ctx the ctx
     */
    public MessagesAdapter(Context ctx){
        this.context = ctx;
    }

    List<Message> messages = new LinkedList<>();

    /**
     * Add message.
     *
     * @param messages the messages
     */
    public void addMessage(List<Message> messages){
        this.messages = messages;
        this.notifyDataSetChanged();
    }

    /**
     * Gets count.
     *
     * @return the count
     */
    @Override
    public int getCount() {
        if(messages == null){
            return 0;
        }
        return messages.size();
    }

    /**
     * Gets item.
     *
     * @param position the position
     * @return the item
     */
    @Override
    public Message getItem(int position) {
        return messages.get(position);
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
     * Gets view.
     *
     * @param position    the position
     * @param convertView the convert view
     * @param parent      the parent
     * @return the view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_message, parent, false);
            vh = new ViewHolder();
            vh.username = (TextView) convertView.findViewById(R.id.msg_user);
            vh.message = (TextView) convertView.findViewById(R.id.msg_message);
            vh.date = (TextView) convertView.findViewById(R.id.msg_date);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.username.setText(messages.get(position).getUsername());
        vh.message.setText(messages.get(position).getMsg());
        try {
            vh.date.setText(DateHelper.getFormattedDate(messages.get(position).getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertView;
    }


    private class ViewHolder{
        TextView username;
        TextView message;
        TextView date;
    }
}
