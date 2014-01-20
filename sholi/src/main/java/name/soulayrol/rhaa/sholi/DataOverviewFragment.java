/*
 * ShoLi, a simple tool to produce short (shopping) lists.
 *
 * Copyright (C) 2013  David Soulayrol
 *
 * ShoLi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ShoLi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package name.soulayrol.rhaa.sholi;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import name.soulayrol.rhaa.sholi.data.Operations;
import name.soulayrol.rhaa.sholi.data.Sholi;


public class DataOverviewFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * The columns we are interested in.
     */
    protected static final String[] PROJECTION = new String[]{
            Sholi.Item._ID,
            Sholi.Item.KEY_NAME,
            Sholi.Item.KEY_STATUS
    };

    /**
     * The common order used to present the items in a list.
     */
    protected static final String ORDER = Sholi.Item.KEY_NAME + " ASC";

    TextView _summary;

    Button _exportButton;

    Button _eraseButton;

    private Cursor _cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data_overview, container, false);

        _summary = (TextView) view.findViewById(R.id.data_overview_text);

        _exportButton = (Button) view.findViewById(R.id.data_export_button);
        _exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder builder = new StringBuilder();
                Intent intent = new Intent(Intent.ACTION_SEND);

                Operations.serialize(_cursor, builder);

                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, R.string.fragment_data_export_subject);
                intent.putExtra(Intent.EXTRA_TEXT, builder.toString());

                startActivity(Intent.createChooser(intent, null));
            }
        });

        _eraseButton = (Button) view.findViewById(R.id.data_erase_button);
        _eraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setMessage(R.string.dialog_erase_all_message)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.dialog_erase_all_title)
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        getActivity().getContentResolver().delete(
                                                Sholi.Item.CONTENT_URI, null, null);
                                    }
                                })
                        .setNegativeButton(android.R.string.no, null);

                builder.create().show();
            }
        });
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), Sholi.Item.CONTENT_URI,
                PROJECTION, null, null, ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        _cursor = cursor;
        _summary.setText(getResources().getQuantityString(
                R.plurals.fragment_data_overview_text, cursor.getCount(), cursor.getCount()));
        _exportButton.setEnabled(true);
        _eraseButton.setEnabled(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        if (_cursor != null) {
            // TODO: What about the displayed information?
            _exportButton.setEnabled(false);
            _eraseButton.setEnabled(false);
        }
    }
}
