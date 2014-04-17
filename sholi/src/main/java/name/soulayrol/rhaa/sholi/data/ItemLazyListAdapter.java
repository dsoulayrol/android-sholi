/*
 * ShoLi, a simple tool to produce short lists.
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
package name.soulayrol.rhaa.sholi.data;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.greenrobot.dao.query.LazyList;
import name.soulayrol.rhaa.sholi.R;
import name.soulayrol.rhaa.sholi.data.model.Checkable;
import name.soulayrol.rhaa.sholi.data.model.Item;


public class ItemLazyListAdapter extends AbstractLazyListAdapter<Item> {

    public ItemLazyListAdapter(Context context, LazyList<Item> lazyList, int textSize) {
        super (context, lazyList, textSize);
    }

    @Override
    public View newView(Context context, Item item, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Item item) {
        TextView name = (TextView) view.findViewById(R.id.item_name);
        name.setText(item.getName());
        name.setTextSize(getTextSize());
        switch (item.getStatus()) {
            case Checkable.OFF_LIST:
                name.setPaintFlags(name.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                name.setTextColor(Color.GRAY);
                break;
            case Checkable.UNCHECKED:
                name.setPaintFlags(name.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                name.setTextColor(Color.GREEN);
                break;
            case Checkable.CHECKED:
                name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                name.setTextColor(Color.WHITE);
                break;
        }
    }
}
