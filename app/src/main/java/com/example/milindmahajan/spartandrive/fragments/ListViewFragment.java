package com.example.milindmahajan.spartandrive.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.milindmahajan.spartandrive.R;
import com.example.milindmahajan.spartandrive.model.DropboxItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by milind.mahajan on 11/30/15.
 */
public class ListViewFragment extends Fragment {

    private View parentView;
    ArrayList <DropboxItem> dropboxItems = new ArrayList<DropboxItem>();
    private ListViewAdapter listViewAdapter;

    private static final int CONTEXTMENU_OPTION_VIEW = 1;
    private static final int CONTEXTMENU_OPTION_DELETE = 2;
    private static final int CONTEXTMENU_OPTION_SHARE = 3;
    private static final int CONTEXTMENU_OPTION_DOWNLOAD = 4;
    private static final int CONTEXTMENU_OPTION_MOVE = 5;
    private static final int CONTEXTMENU_OPTION_COPY = 6;
    private static final int CONTEXTMENU_OPTION_CANCEL = 7;

    ListViewFragmentProtocol listViewFragmentListener;
    public interface  ListViewFragmentProtocol {

        public void viewDropboxItem(DropboxItem dropboxItem);

        public void deleteDropboxItems(ArrayList <DropboxItem> toBeDeleted);
        public void shareFromDropbox(ArrayList <DropboxItem> toBeShared);

        public void moveDropboxItem(ArrayList <DropboxItem> toBeMoved);
        public void copyDropboxItem(ArrayList <DropboxItem> toBeMoved);

        public void beginContextualActionMode(ArrayList <DropboxItem> selectedItems);
        public void endContextualActionMode();
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

        addClickListener();

        return parentView;
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
        menu.add(Menu.NONE, CONTEXTMENU_OPTION_CANCEL, 6, "Cancel");
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

    public void reloadListView (ArrayList<DropboxItem> dropboxItems) {

        this.dropboxItems.removeAll(this.dropboxItems);
        this.dropboxItems.addAll(dropboxItems);

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
            title.setTextColor(Color.parseColor("#424242"));

            TextView modified = (TextView)convertView.findViewById(R.id.modified);
            modified.setText(dropboxItem.getModified());

            TextView size = (TextView)convertView.findViewById(R.id.size);
            size.setText(dropboxItem.getSize());
            size.setVisibility(dropboxItem.isDir() ? View.INVISIBLE : View.VISIBLE);

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