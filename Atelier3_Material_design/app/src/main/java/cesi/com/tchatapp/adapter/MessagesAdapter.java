package cesi.com.tchatapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

  private final Context context;

  /**
   * Instantiates a new Messages adapter.
   *
   * @param ctx the ctx
   */
  public MessagesAdapter(Context ctx) {
    this.context = ctx;
  }

  List<Message> messages = new LinkedList<>();

  /**
   * Add message.
   *
   * @param messages the messages
   */
  public void addMessage(List<Message> messages) {
    this.messages = messages;
    this.notifyDataSetChanged();
  }

  /**
   * On create view holder messages adapter . view holder.
   *
   * @param parent the parent
   * @param i      the
   * @return the messages adapter . view holder
   */
  @Override
  public MessagesAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int i) {
    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
    View convertView = inflater.inflate(R.layout.item_message, parent, false);
    ViewHolder vh = new ViewHolder(convertView);
    return vh;
  }

  /**
   * On bind view holder.
   *
   * @param vh       the vh
   * @param position the position
   */
  @Override
  public void onBindViewHolder(final MessagesAdapter.ViewHolder vh, final int position) {
    vh.username.setText(messages.get(position).getUsername());
    vh.message.setText(messages.get(position).getMsg());
    try {
      vh.date.setText(DateHelper.getFormattedDate(messages.get(position).getDate()));
    } catch (ParseException e) {
      e.printStackTrace();
    }
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
    if (messages == null) {
      return 0;
    }
    return messages.size();
  }


  /**
   * The type View holder.
   */
  public static class ViewHolder extends RecyclerView.ViewHolder{
    TextView username;
    TextView message;
    TextView date;

    /**
     * Instantiates a new View holder.
     *
     * @param itemView the item view
     */
    public ViewHolder(final View itemView) {
      super(itemView);
      username = (TextView) itemView.findViewById(R.id.msg_user);
      message = (TextView) itemView.findViewById(R.id.msg_message);
      date = (TextView) itemView.findViewById(R.id.msg_date);
    }
  }
}
