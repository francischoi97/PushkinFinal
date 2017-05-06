package com.pushkin.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pushkin.ConversationPreview;
import com.pushkin.PushkinAdapter;
import com.pushkin.PushkinDatabaseHelper;
import com.pushkin.R;
import com.pushkin.activity.MainActivity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConversationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConversationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConversationsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DB_HELPER = "dbHelper";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    public static PushkinDatabaseHelper dbHelper;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View rootView;
    private FrameLayout frameLayout;

    public ConversationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConversationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConversationsFragment newInstance(String param1, String param2) {
        ConversationsFragment fragment = new ConversationsFragment();
//        Bundle args = new Bundle();
//        args.putSerializable(DB_HELPER, dbHelper);
////        args.putString(ARG_PARAM2, param2);
////        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//        linearLayout = (LinearLayout) rootView.findViewById(R.id.ll_conversations);
//        TextView tv = (TextView) linearLayout.findViewById(R.id.tv);
//        tv.setText("this is me");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_conversations, container, false);
//        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.frag_conversations);
//        System.out.println (linearLayout);
//        TextView tv = (TextView) linearLayout.findViewById(R.id.tv);
//        tv.setText("Hey");
//        System.out.println("Added textView");

        ListView listView = (ListView) rootView.findViewById(R.id.conversations_listView);
        dbHelper = MainActivity.dbHelper;
        System.out.println(dbHelper);
        dbHelper.populate();
        ArrayList<ConversationPreview> previews = dbHelper.getConversationPreviews();
        System.out.println ("Size of previews " + previews.size());
//        System.out.println (previews);
        PushkinAdapter adapter = new PushkinAdapter(getActivity(), R.layout.conversation_preview, previews);
        listView.setAdapter(adapter);
        return rootView;
    }

//    public void onActivityCreated (Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
