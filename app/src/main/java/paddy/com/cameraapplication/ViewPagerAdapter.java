package paddy.com.cameraapplication;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Shrikant on 26-07-2015.
 */
public class ViewPagerAdapter extends PagerAdapter {
    // Declare Variables
    Context context;
    int[] rings_list;
    LayoutInflater inflater;

    public ViewPagerAdapter(Context context,int[] rings_list) {
        this.context = context;
        this.rings_list = rings_list;
    }

    @Override
    public int getCount() {
        return rings_list.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((FrameLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ImageView imgrings_list;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.custom_image_row, container,
                false);

        // Locate the ImageView in viewpager_item.xml
        imgrings_list = (ImageView) itemView.findViewById(R.id.middle_img);
        // Capture position and set to the ImageView
        imgrings_list.setImageResource(rings_list[position]);
        imgrings_list.setOnTouchListener(new Touch());

        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((FrameLayout) object);

    }
}
