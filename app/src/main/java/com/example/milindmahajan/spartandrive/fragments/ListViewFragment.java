package com.example.milindmahajan.spartandrive.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.example.milindmahajan.spartandrive.R;
import com.example.milindmahajan.spartandrive.model.DropboxItem;
import com.example.milindmahajan.spartandrive.utils.Common;
import com.example.milindmahajan.spartandrive.utils.SearchTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by milind.mahajan on 11/30/15.
 */
public class ListViewFragment extends Fragment{

    private View parentView;
    ArrayList <DropboxItem> dropboxItems = new ArrayList<DropboxItem>();
    private ListViewAdapter listViewAdapter;

    private EditText searchField;
    private static final int CONTEXTMENU_OPTION_VIEW = 1;
    private static final int CONTEXTMENU_OPTION_DELETE = 2;
    private static final int CONTEXTMENU_OPTION_SHARE = 3;
    private static boolean searchMode;


    private static final int CONTEXTMENU_OPTION_DOWNLOAD = 4;
    private static final int CONTEXTMENU_OPTION_MOVE = 5;
    private static final int CONTEXTMENU_OPTION_COPY = 6;
    private static final int CONTEXTMENU_OPTION_RENAME = 7;
    private static final int CONTEXTMENU_OPTION_CANCEL = 8;

    ListViewFragmentProtocol listViewFragmentListener;
    public interface  ListViewFragmentProtocol {

        public void viewDropboxItem(DropboxItem dropboxItem);

        public void deleteDropboxItems(ArrayList <DropboxItem> toBeDeleted);
        public void shareFromDropbox(ArrayList <DropboxItem> toBeShared);

        public void moveDropboxItem(ArrayList <DropboxItem> toBeMoved);
        public void copyDropboxItem(ArrayList <DropboxItem> toBeMoved);

        public void renameDropboxItem(DropboxItem item);

        public void beginContextualActionMode(ArrayList <DropboxItem> selectedItems);
        public void endContextualActionMode();

        public DropboxItem getRootFolder();
        public void refreshRootFolder();
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        try {

            listViewFragmentListener = (ListViewFragmentProtocol) context;
        } catch (ClassCastException exception) {

            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        parentView = inflater.inflate(R.layout.fragment_list_view, container, false);

        ListView listView = (ListView)parentView.findViewById(R.id.list_view);
        registerForContextMenu(listView);

        searchField = (EditText) parentView.findViewById(R.id.searchField);
        addTextChangeListener();
        addClickListener();

        return parentView;
    }

    public boolean isSearchModeOn () {

        return searchMode;
    }
    public static void hideSoftKeyboard (Activity activity, View view) {

        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    private void addTextChangeListener() {

        final EditText searchEditText = (EditText) parentView.findViewById(R.id.searchField);

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                hideSoftKeyboard(getActivity(), parentView);
                String searchQuery = searchEditText.getText().toString();

                if (searchQuery.length() > 0) {

                    searchMode = Boolean.TRUE;
                    SearchTask s = (SearchTask) new SearchTask(new SearchTask.AsyncResponse() {
                        @Override
                        public void processFinish(ArrayList<DropboxAPI.Entry> output) {

                            ArrayList<DropboxItem> result = new ArrayList<DropboxItem>();

                            Log.d("search results", "reached here");
                            for (DropboxAPI.Entry e : output) {

                                DropboxItem dropboxItem = new DropboxItem(e);
                                result.add(dropboxItem);
                            }

                            reloadListView(result);
                        }
                    }, listViewFragmentListener.getRootFolder().getPath(), searchQuery).execute();
                } else {
                    searchMode = Boolean.FALSE;
                    listViewFragmentListener.refreshRootFolder();
                }
                if (actionId == EditorInfo.IME_ACTION_SEND ||
                        actionId == EditorInfo.IME_ACTION_GO ||
                        actionId == EditorInfo.IME_ACTION_DONE) {

                    hideSoftKeyboard(getActivity(), parentView);

                    return true;
                }
                return false;
            }
        });
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(this.dropboxItems.get(contextMenuInfo.position).getName());

        menu.add(Menu.NONE, CONTEXTMENU_OPTION_VIEW, 0, "View");
        menu.add(Menu.NONE, CONTEXTMENU_OPTION_DELETE, 1, "Delete");
        menu.add(Menu.NONE, CONTEXTMENU_OPTION_SHARE, 2, "Share");
        menu.add(Menu.NONE, CONTEXTMENU_OPTION_DOWNLOAD, 3, "Download");
        menu.add(Menu.NONE, CONTEXTMENU_OPTION_MOVE, 4, "Move");
        menu.add(Menu.NONE, CONTEXTMENU_OPTION_COPY, 5, "Copy");
        if (!this.dropboxItems.get(contextMenuInfo.position).isDir()) {

            menu.add(Menu.NONE, CONTEXTMENU_OPTION_RENAME, 6, "Rename");
        }
        menu.add(Menu.NONE, CONTEXTMENU_OPTION_CANCEL, 7, "Cancel");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        switch (item.getItemId()) {

            case CONTEXTMENU_OPTION_VIEW:

                listViewFragmentListener.viewDropboxItem(dropboxItems.get(contextMenuInfo.position));
                break;

            case CONTEXTMENU_OPTION_DELETE:

                ArrayList <DropboxItem> itemsToDelete = new ArrayList<DropboxItem>();
                itemsToDelete.add(this.dropboxItems.get(contextMenuInfo.position));
                listViewFragmentListener.deleteDropboxItems(itemsToDelete);
                break;

            case CONTEXTMENU_OPTION_SHARE:

                ArrayList <DropboxItem> selectedItemToShare = new ArrayList<DropboxItem>();
                selectedItemToShare.add(this.dropboxItems.get(contextMenuInfo.position));
                listViewFragmentListener.shareFromDropbox(selectedItemToShare);
                break;

            case CONTEXTMENU_OPTION_DOWNLOAD:

                break;

            case CONTEXTMENU_OPTION_MOVE:

                ArrayList <DropboxItem> itemsToMove = new ArrayList<DropboxItem>();
                itemsToMove.add(this.dropboxItems.get(contextMenuInfo.position));
                listViewFragmentListener.moveDropboxItem(itemsToMove);
                break;

            case CONTEXTMENU_OPTION_COPY:

                ArrayList <DropboxItem> itemsToCopy = new ArrayList<DropboxItem>();
                itemsToCopy.add(this.dropboxItems.get(contextMenuInfo.position));
                listViewFragmentListener.copyDropboxItem(itemsToCopy);
                break;

            case CONTEXTMENU_OPTION_RENAME:

                listViewFragmentListener.renameDropboxItem(this.dropboxItems.get(contextMenuInfo.position));
                break;

            case CONTEXTMENU_OPTION_CANCEL:

                break;
        }

        return true;
    }

    private void addClickListener() {

        ListView listView = (ListView) parentView.findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {

                listViewFragmentListener.viewDropboxItem(dropboxItems.get(pos));
            }
        });
    }

    private ArrayList<DropboxItem> sortDropboxItems(ArrayList <DropboxItem> unsortedArray) {

        ArrayList <DropboxItem> sortedItems = new ArrayList<DropboxItem>();

        sortedItems.addAll(getSortedFolders(unsortedArray));
        sortedItems.addAll(getSortedFiles(unsortedArray));

        return sortedItems;
    }

    private ArrayList<DropboxItem> getSortedFolders(ArrayList <DropboxItem> unsortedArray) {

        ArrayList <DropboxItem> folders = new ArrayList<DropboxItem>();

        for (DropboxItem item : unsortedArray) {

            if (item.isDir()) {

                folders.add(item);
            }
        }

        Collections.sort(folders, new DropboxItem());
        return folders;
    }

    private ArrayList<DropboxItem> getSortedFiles(ArrayList <DropboxItem> unsortedArray) {

        ArrayList <DropboxItem> files = new ArrayList<DropboxItem>();

        for (DropboxItem item : unsortedArray) {

            if (!item.isDir()) {

                files.add(item);
            }
        }

        Collections.sort(files, new DropboxItem());
        return files;
    }

    public void reloadListView (ArrayList<DropboxItem> dropboxItems) {

        ArrayList sortedItems = sortDropboxItems(dropboxItems);
        this.dropboxItems.removeAll(this.dropboxItems);
        this.dropboxItems.addAll(sortedItems);

        reloadData(this.dropboxItems);
    }

    private void reloadData(ArrayList <DropboxItem> dropboxItems) {

        listViewAdapter = new ListViewAdapter(getContext(),
                R.layout.dropbox_item, 0, dropboxItems);


        ListView listView = (ListView)parentView.findViewById(R.id.list_view);
        listView.setAdapter(listViewAdapter);
    }

    public ArrayList <DropboxItem> selectedDropboxItems () {

        return listViewAdapter.selectedFiles;
    }

    private class ListViewAdapter extends ArrayAdapter<DropboxItem> {

        ArrayList <DropboxItem> selectedFiles = new ArrayList<DropboxItem>();

        public ListViewAdapter(Context context, int resource, int textViewResourceId, List<DropboxItem> objects) {

            super(context, resource, textViewResourceId, objects);
        }

        public void setNewSelection(DropboxItem dropboxItem) {

            selectedFiles.add(dropboxItem);
            notifyDataSetChanged();
        }

        public void removeSelection(DropboxItem dropboxItem) {

            for (int i = 0; i < selectedFiles.size(); i++) {

                if (selectedFiles.get(i).getPath().equals(dropboxItem.getPath())) {

                    selectedFiles.remove(i);
                    notifyDataSetChanged();

                    break;
                }
            }
        }

        public ArrayList <DropboxItem> getSelectedFiles () {

            return this.selectedFiles;
        }

        public boolean isSelected (DropboxItem dropboxItem) {

            for (DropboxItem dbItem : selectedFiles) {

                if (dropboxItem.getPath().equals(dbItem.getPath())) {

                    return true;
                }
            }

            return false;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if(convertView == null) {

                convertView = getActivity().getLayoutInflater().inflate(R.layout.dropbox_item, parent, false);
            }

            DropboxItem dropboxItem = dropboxItems.get(position);

            ImageView imageView = (ImageView)convertView.findViewById(R.id.icon);
            imageView.setImageResource(dropboxItem.getIcon());

            TextView title = (TextView)convertView.findViewById(R.id.title);
            title.setText(dropboxItem.getName());
            title.setTextColor(Color.parseColor("#FF652F"));

            TextView itemInfoToggle = (TextView)convertView.findViewById(R.id.modified);
            itemInfoToggle.setTextColor(Color.parseColor("#666666"));
            if(searchMode)
            {
                itemInfoToggle.setText("in ..." + dropboxItem.getParentPath());
                itemInfoToggle.setTypeface(null, Typeface.ITALIC);
            }
            else
            {
                itemInfoToggle.setText(dropboxItem.getModified());
                itemInfoToggle.setTypeface(null, Typeface.NORMAL);
            }

            TextView size = (TextView)convertView.findViewById(R.id.size);
            size.setText(dropboxItem.getSize());
            size.setTextColor(Color.parseColor("#666666"));
            if(searchMode) {

                size.setVisibility(View.INVISIBLE);
            } else {

                size.setVisibility(dropboxItem.isDir() ? View.INVISIBLE : View.VISIBLE);
            }

            final CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkBox);
            checkBox.setChecked(listViewAdapter.isSelected(dropboxItems.get(position)));

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked) {

                        listViewAdapter.setNewSelection(dropboxItems.get(position));
                    } else {

                        listViewAdapter.removeSelection(dropboxItems.get(position));
                    }

                    if (listViewAdapter.getSelectedFiles().size() != 0) {

                        listViewFragmentListener.beginContextualActionMode(selectedFiles);
                    } else {

                        listViewFragmentListener.endContextualActionMode();
                    }
                }
            });

            return convertView;
        }
    }
}