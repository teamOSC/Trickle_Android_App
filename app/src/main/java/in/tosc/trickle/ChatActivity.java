package in.tosc.trickle;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class ChatActivity extends ActionBarActivity {

    private RecyclerView chatRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Chat> chatList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);
        chatRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(mLayoutManager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public RelativeLayout mRelativeLayout;
            public TextView chatTextVew;
            public ViewHolder(RelativeLayout v) {
                super(v);
                mRelativeLayout = v;
                chatTextVew = (TextView) mRelativeLayout.findViewById(R.id.chat_message);
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_row, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            Chat chat = chatList.get(position);
            if (chat.isMine()) {
                holder.mRelativeLayout.setBackgroundResource(R.drawable.speech_bubble_green);
            } else {
                holder.mRelativeLayout.setBackgroundResource(R.drawable.speech_bubble_orange);
            }
            holder.chatTextVew.setText(chatList.get(position).getMessage());
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return chatList.size();
        }
    }

    private static class Chat {
        private boolean isMine;
        private String message;

        public Chat (boolean isMine, String message) {
            this.isMine = isMine;
            this.message = message;
        }

        public boolean isMine() {
            return isMine;
        }

        public String getMessage() {
            return message;
        }
    }
}
