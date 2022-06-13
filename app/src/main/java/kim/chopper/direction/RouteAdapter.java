package kim.chopper.direction;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import kim.chopper.direction.data.model.Route;

public class RouteAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List<Route> list;

    public RouteAdapter(Context context, List<Route> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.route_item, null);
        ImageView icon = view.findViewById(R.id.icon);
        TextView transport = view.findViewById(R.id.transport);
        TextView transposition = view.findViewById(R.id.transposition);
        TextView time = view.findViewById(R.id.time);
        TextView price = view.findViewById(R.id.price);

        icon.setImageResource(list.get(i).getIcon());
        transport.setText(list.get(i).getTransport());
        transposition.setText(list.get(i).getTransposition());
        time.setText(list.get(i).getTime());
        price.setText(list.get(i).getPrice());

        return view;
    }
}
