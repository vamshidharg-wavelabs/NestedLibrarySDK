package com.cometchat.pro.uikit.ui_components.groups.group_list;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.uikit.ui_components.shared.cometchatGroups.CometChatGroups;

import java.util.ArrayList;
import java.util.List;

import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.cometchat.pro.uikit.ui_resources.utils.item_clickListener.OnItemClickListener;
import com.cometchat.pro.uikit.ui_components.groups.create_group.CometChatCreateGroupActivity;
import com.cometchat.pro.uikit.ui_resources.utils.FontUtils;
import com.cometchat.pro.uikit.ui_settings.UISettings;
import com.cometchat.pro.uikit.ui_resources.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants.IntentStrings.PINNED_GROUPS;
import static com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants.IntentStrings.PINNED_GROUPS_MAX_LIMIT;

/*

* Purpose - CometChatGroupList class is a fragment used to display list of groups and perform certain action on click of item.
            It also provide search bar to search group from the list.

* Created on - 20th December 2019

* Modified on  - 23rd March 2020

*/

public class CometChatGroupList extends Fragment  {

    private static OnItemClickListener event;

    private CometChatGroups rvGroupList;   //Uses to display list of groups.

    private GroupsRequest groupsRequest;    //Uses to fetch Groups.

    private EditText etSearch;    //Uses to perform search operations on groups.

    private ImageView clearSearch;

    private ImageView ivCreateGroup;

    private LinearLayout noGroupLayout;

    private List<Group> groupList = new ArrayList<>();

    private static final String TAG = "CometChatGroupListScreen";

    private boolean isEnable = false;

    private static String PIN = "Pin";
    private static String UNPIN = "Un pin";

    public CometChatGroupList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_cometchat_grouplist, container, false);
        TextView title = view.findViewById(R.id.tv_title);
        title.setTypeface(FontUtils.getInstance(getActivity()).getTypeFace(FontUtils.robotoMedium));
        rvGroupList=view.findViewById(R.id.rv_group_list);
        noGroupLayout = view.findViewById(R.id.no_group_layout);
        etSearch = view.findViewById(R.id.search_bar);
        clearSearch = view.findViewById(R.id.clear_search);

        ivCreateGroup = view.findViewById(R.id.create_group);
        ivCreateGroup.setImageTintList(ColorStateList.valueOf(Color.parseColor(UISettings.getColor())));

        if(UISettings.isGroupCreate() && CometChat.getLoggedInUser().getRole().toLowerCase().equals("admin"))
            ivCreateGroup.setVisibility(View.VISIBLE);
        else
            ivCreateGroup.setVisibility(View.GONE);

        if(Utils.isDarkMode(getContext())) {
            title.setTextColor(getResources().getColor(R.color.textColorWhite));
        } else {
            title.setTextColor(getResources().getColor(R.color.primaryTextColoruikit));
        }

        ivCreateGroup.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), CometChatCreateGroupActivity.class);
            startActivity(intent);
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()==0) {
                    // if etSearch is empty then fetch all groups.
                    groupsRequest=null;
                    rvGroupList.clear();
                    fetchGroup();
                }
                else {
                    // Search group based on text in etSearch field.
                    searchGroup(editable.toString());
                }
            }
        });
        etSearch.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH)
                {
                    searchGroup(textView.getText().toString());
                    clearSearch.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.setText("");
                clearSearch.setVisibility(View.GONE);
                searchGroup(etSearch.getText().toString());
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                // Hide the soft keyboard
                inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(),0);
            }
        });

        //Uses to fetch next list of group if rvGroupList (RecyclerView) is scrolled in upward direction.
        rvGroupList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if (!recyclerView.canScrollVertically(1)) {
                    fetchGroup();
                }

            }
        });

        // Used to trigger event on click of group item in rvGroupList (RecyclerView)
        rvGroupList.setItemClickListener(new OnItemClickListener<Group>() {
            @Override
            public void OnItemClick(Group group, int position) {
                if (event!=null)
                    event.OnItemClick(group,position);
            }

            @Override
            public void OnItemLongClick(Group var, int position) {

                if(!isItemPinned(view, position))
                    alertDialogForPin(view, position, PIN);
                else
                    alertDialogForPin(view, position, UNPIN);
            }
        });
        return view;
    }

    public boolean isItemPinned(View view, int position){
        JSONObject metadata = CometChat.getLoggedInUser().getMetadata();

        try {
            if (metadata != null) {
                if(metadata.has(PINNED_GROUPS) && metadata.getJSONArray(PINNED_GROUPS).length() > 0){
                    JSONArray pinnedGroupIDs = metadata.getJSONArray(PINNED_GROUPS);
                    for (int i = 0; i < pinnedGroupIDs.length(); i++)
                        if (pinnedGroupIDs.getString(i).equals(groupList.get(position).getGuid()))
                            return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public void alertDialogForPin(View view, int position, String action){
        String groupName = groupList.get(position).getName();
        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
        dialog.setMessage("Do you want to pin "+groupName+"?");
        dialog.setTitle(action + " Group");
        dialog.setPositiveButton(action,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        if(action.equals(PIN))
                            pinGroup(view, position);
                        else
                            unPinGroup(view, position);
                    }
                });
        dialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // custom code
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

    public void unPinGroup(View view, int position){
        User user = CometChat.getLoggedInUser();
        JSONArray pinnedGroups = null;

        try {
            pinnedGroups = user.getMetadata().getJSONArray(PINNED_GROUPS);
            String groupToUnpin = groupList.get(position).getGuid();
            int indexToRemove = -1;

            for (int i = 0; i < pinnedGroups.length(); i++){
                if(pinnedGroups.get(i).equals(groupToUnpin)){
                    indexToRemove = i;
                    break;
                }
            }

            if(indexToRemove >= 0) {
                pinnedGroups.remove(indexToRemove);
                user.getMetadata().put(PINNED_GROUPS, pinnedGroups);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CometChat.updateUser(user, UIKitConstants.AppInfo.AUTH_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                if (getContext()!=null)
                    Toast.makeText(getContext(),"Unpin Group successful",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(CometChatException e) {
                if (getContext()!=null)
                    Toast.makeText(getContext(),"Error unpinning the group",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void pinGroup(View view, int position){
        User user = CometChat.getLoggedInUser();

        JSONObject userMetadataObject = null;
        try {
            if(user.getMetadata() == null){ // Has no metadata at all

                    try {
                        JSONArray pinnedGroups = new JSONArray();
                        pinnedGroups.put(groupList.get(position).getGuid());
                        // create metadata and add the list to it
                        userMetadataObject = new JSONObject();
                        userMetadataObject.put(PINNED_GROUPS, pinnedGroups);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }else if(!user.getMetadata().has(PINNED_GROUPS)) { // Has metadata but no pinned groups key-value pair

                    userMetadataObject = user.getMetadata();
                    // Create pinned groups list
                    JSONArray pinnedGroups = new JSONArray();
                    pinnedGroups.put(groupList.get(position).getGuid());
                    // add list to metaadata
                    userMetadataObject.put(PINNED_GROUPS, pinnedGroups);
            }else{ // Has metadata and pinned groups

                if(user.getMetadata().getJSONArray(PINNED_GROUPS).length() < PINNED_GROUPS_MAX_LIMIT) {
                    userMetadataObject = user.getMetadata();
                    userMetadataObject.getJSONArray(PINNED_GROUPS).put(groupList.get(position).getGuid());
                }else{
                    Toast.makeText(getContext(),
                            "Max limit for pinning groups is " + PINNED_GROUPS_MAX_LIMIT,
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }
            }

        } catch (JSONException e) {
                e.printStackTrace();
            }

        user.setMetadata(userMetadataObject);

        CometChat.updateUser(user, UIKitConstants.AppInfo.AUTH_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                if (getContext()!=null)
                    Toast.makeText(getContext(),"Pin Group successful",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(CometChatException e) {
                if (getContext()!=null)
                    Toast.makeText(getContext(),"Error pinning the group",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchGroup();
    }

    /**
     *  This method is used to retrieve list of groups present in your App_ID.
     *  For more detail please visit our official documentation {@link "https://prodocs.cometchat.com/docs/android-groups-retrieve-groups" }
     *
     * @see GroupsRequest
     */
    private void fetchGroup(){
        if (groupsRequest==null){
            groupsRequest=new GroupsRequest.GroupsRequestBuilder().setLimit(30).build();
        }
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> groups) {
                List<Group> filteredList = filterGroup(groups);
                rvGroupList.setGroupList(filteredList); // sets the groups in rvGroupList i.e CometChatGroupList Component.
                groupList.addAll(groups);
                if (groupList.size()==0) {
                    noGroupLayout.setVisibility(View.VISIBLE);
                    rvGroupList.setVisibility(View.GONE);
                } else {
                    noGroupLayout.setVisibility(View.GONE);
                    rvGroupList.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onError(CometChatException e) {
                if (rvGroupList!=null)
                    Utils.showCometChatDialog(getContext(),
                            rvGroupList,getResources().getString(R.string.group_list_error),true);
            }
        });
    }
    private List<Group> filterGroup(List<Group> groups) {
        List<Group> resultList = new ArrayList<>();
        for (Group group : groups) {
            if (group.isJoined()) {
                resultList.add(group);
            }
            if (UISettings.getGroupListing()
                    .equalsIgnoreCase("public_groups") &&
                    group.getGroupType().equalsIgnoreCase(CometChatConstants.GROUP_TYPE_PUBLIC)) {
                resultList.add(group);
            } else if (UISettings.getGroupListing()
                    .equalsIgnoreCase("password_protected_groups") &&
                    group.getGroupType().equalsIgnoreCase(CometChatConstants.GROUP_TYPE_PASSWORD)) {
                resultList.add(group);
            } else if (UISettings.getGroupListing()
                    .equalsIgnoreCase("public_and_password_protected_groups")) {
                resultList.add(group);
            }
        }
        return resultList;
    }

    /**
     *  This method is used to search groups present in your App_ID.
     *  For more detail please visit our official documentation {@link "https://prodocs.cometchat.com/docs/android-groups-retrieve-groups" }
     *
     * @param s is a string used to get groups matches with this string.
     * @see GroupsRequest
     */
    private void searchGroup(String s)
    {
        GroupsRequest groupsRequest = new GroupsRequest.GroupsRequestBuilder().setSearchKeyWord(s).setLimit(100).build();
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> groups) {
                rvGroupList.searchGroupList(groups); // sets the groups in rvGroupList i.e CometChatGroupList Component.
            }


            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: "+e.getMessage());
            }
        });
    }


    /**
     *
     * @param groupItemClickListener An object of <code>OnItemClickListener&lt;T&gt;</code> abstract class helps to initialize with events
     *                               to perform onItemClick & onItemLongClick.
     * @see OnItemClickListener
     */
    public static void setItemClickListener(@NonNull OnItemClickListener<Group> groupItemClickListener){
        event=groupItemClickListener;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        groupsRequest=null;
    }
}
