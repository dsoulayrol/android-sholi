/*
 * ShoLi, a simple tool to produce short (shopping) lists.
 *
 * Copyright (C) 2014  David Soulayrol
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

import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Semaphore;

import name.soulayrol.rhaa.sholi.data.Operations;
import name.soulayrol.rhaa.sholi.data.Sholi;


public class ImportFragment extends DialogFragment {

    private static final String ARG_DATA = "data";

    private Button _button;

    private TextView _summary;

    private ProgressBar _progress;

    private WeakReference<ImportTask> _taskRef;

    public static ImportFragment newInstance(String data) {
        ImportFragment fragment = new ImportFragment();

        Bundle args = new Bundle();
        args.putString(ARG_DATA, data);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        ImportTask task = new ImportTask(getArguments().getString(ARG_DATA));
        _taskRef = new WeakReference<ImportTask>(task);
        task.execute();

        setCancelable(false);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data_import, container, false);

        _button = (Button) view.findViewById(R.id.data_import_button);
        _button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        _summary = (TextView) view.findViewById(R.id.data_import_text);
        _progress = (ProgressBar) view.findViewById(R.id.data_import_progress);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        int height = getResources().getDimensionPixelSize(R.dimen.fragment_import_dialog_height);
        int width = getResources().getDimensionPixelSize(R.dimen.fragment_import_dialog_width);
        getDialog().getWindow().setLayout(width, height);

        if (_taskRef != null && _taskRef.get() != null)
            _taskRef.get().startReporting();
    }

    private class ImportResult {
        private int imported;
        private int ignored;

        public int getIgnored() {
            return ignored;
        }

        public void addIgnored() {
            ++ignored;
        }

        public int getImported() {
            return imported;
        }

        public void addImported() {
            ++imported;
        }
    }

    /**
     * Handle a serialized list.
     *
     * The import task uses no transaction: it parses and inserts items with best effort.
     */
    private class ImportTask extends AsyncTask<Void, Integer, ImportResult> {

        private String _data;
        private int _dataSize;
        private Semaphore _fragmentView = new Semaphore(1);

        public ImportTask(String data) {
            _data = data;
        }

        public void startReporting() {
            _fragmentView.release();
        }

        @Override
        protected ImportResult doInBackground(Void... params) {
            ContentResolver content = getActivity().getContentResolver();
            List<Operations.TransientItem> items = Operations.deserialize(_data);
            ImportResult result = new ImportResult();

            try {
                _fragmentView.acquire();
            } catch (InterruptedException e) {
                // We can safely ignore this.
                Log.w(Sholi.TAG, e);
            }

            _dataSize = items.size();
            publishProgress(0);

            for (Operations.TransientItem item: items) {
                ContentValues values = new ContentValues();

                values.put(Sholi.Item.KEY_NAME, item.name);
                values.put(Sholi.Item.KEY_STATUS, item.status);
                if (content.insert(Sholi.Item.CONTENT_URI, values) != null)
                    result.addImported();
                else
                    result.addIgnored();

                publishProgress(1);
            }

            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            if (progress[0] == 0) {
                _progress.setMax(_dataSize);
                _summary.setText(getResources().getQuantityString(
                        R.plurals.fragment_data_import_text, _dataSize, _dataSize));
            }
            else
                _progress.incrementProgressBy(progress[0]);
        }

        @Override
        protected void onPostExecute(ImportResult result) {
            StringBuilder builder = new StringBuilder();

            builder.append(getResources().getString(R.string.fragment_data_import_conclusion));
            builder.append('\n');
            builder.append(getResources().getQuantityString(
                    R.plurals.fragment_data_import_success_text,
                    result.getImported(), result.getImported()));
            builder.append(getResources().getQuantityString(
                    R.plurals.fragment_data_import_ignored_text,
                    result.getIgnored(), result.getIgnored()));

            _button.setEnabled(true);
            _summary.setText(builder.toString());
        }
    }
}
