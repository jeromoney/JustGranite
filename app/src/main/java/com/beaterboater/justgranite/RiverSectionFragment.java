package com.beaterboater.justgranite;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beaterboater.justgranite.R;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RiverSectionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RiverSectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RiverSectionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mGaugeId;

    private GraniteViewModel model;


    public RiverSectionFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RiverSectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RiverSectionFragment newInstance(String param1, String param2) {
        RiverSectionFragment fragment = new RiverSectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(GraniteViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Grab out river section specific data
        int position = 0;
        if (getArguments() != null) {
            position = getArguments().getInt("position");
        }

        RiverSection riverSection = RiverSectionJsonUtil.getRiverSection(getContext(), position);
        mGaugeId = riverSection.getId();

        // Inflate the layout for this fragment
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_river_section, container, false);
        View view = binding.getRoot();
        model.getmStreamValues().observe(getViewLifecycleOwner(), item -> {
            TextView flowView = Objects.requireNonNull(this.getView()).findViewById(R.id.section_flow);
            String flow;
            if (item.get(mGaugeId) == null){
                flow = "0";
            }
            else {
                flow = item.get(mGaugeId).getmFlow().toString();
            }
            String displayStr = String.format(getString(R.string.cfs_format),flow);
            flowView.setText(displayStr);

        });


        binding.setVariable(BR.riversection, riverSection);
        //TODO - switch to databinding
        ImageView imageView = view.findViewById(R.id.riverSectionImage);
        switch (riverSection.getSection_name()){
            case "Numbers":
                imageView.setImageResource(R.drawable.numbers);
                break;
            case "Gore":
                imageView.setImageResource(R.drawable.gore);
                break;
            case "Bailey":
                imageView.setImageResource(R.drawable.bailey);
                break;
            default:
        }
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

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
