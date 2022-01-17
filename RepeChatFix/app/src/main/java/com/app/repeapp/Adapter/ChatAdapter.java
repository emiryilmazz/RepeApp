package com.app.repeapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.repeapp.Model.Chat;
import com.app.repeapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {
    private static final int MESAJ_SOL = 1;
    private static final int MESAJ_SAG = 0;

    private ArrayList<Chat> mChatList;
    private Context mContext;
    private String mUID;
    private String hedefProfil;
    private View v;
    private Chat mChat;

    public ChatAdapter(ArrayList<Chat> mChatList, Context mContext, String mUID, String hedefProfil) {
        this.mChatList = mChatList;
        this.mContext = mContext;
        this.mUID = mUID;
        this.hedefProfil = hedefProfil;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MESAJ_SOL)
            v = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
        else if (viewType == MESAJ_SAG)
            v = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);

        return new ChatHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatHolder holder, int position) {
        mChat = mChatList.get(position);
        if (mChat.getMesajTipi().equals("text")){
            holder.mProgress.setVisibility(View.GONE);
            holder.imgResim.setVisibility(View.GONE);
            holder.txtMesaj.setText(mChat.getMesajIcerigi());
        }else{
            holder.txtMesaj.setVisibility(View.GONE);
            Picasso.get().load(mChat.getMesajIcerigi()).resize(200, 200).into(holder.imgResim, new Callback() {
                @Override
                public void onSuccess() {
                    holder.mProgress.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        }

        if (!mChat.getGonderen().equals(mUID)){
            if (hedefProfil.equals("default"))
                holder.imgProfil.setImageResource(R.mipmap.ic_launcher);
            else
                Picasso.get().load(hedefProfil).resize(56, 56).into(holder.imgProfil);
        }
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder{
        CircleImageView imgProfil;
        TextView txtMesaj;
        ImageView imgResim;
        ProgressBar mProgress;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);

            imgProfil = itemView.findViewById(R.id.chat_item_imgProfil);
            txtMesaj = itemView.findViewById(R.id.chat_item_txtMesaj);
            imgResim = itemView.findViewById(R.id.chat_item_imgResim);
            mProgress = itemView.findViewById(R.id.chat_item_progress);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mChatList.get(position).getGonderen().equals(mUID))
            return MESAJ_SAG;
        else
            return MESAJ_SOL;
    }
}
