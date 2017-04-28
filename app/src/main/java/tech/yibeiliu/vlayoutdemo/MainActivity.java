package tech.yibeiliu.vlayoutdemo;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.ColumnLayoutHelper;
import com.alibaba.android.vlayout.layout.FixLayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.alibaba.android.vlayout.layout.OnePlusNLayoutHelper;
import com.alibaba.android.vlayout.layout.OnePlusNLayoutHelperEx;
import com.alibaba.android.vlayout.layout.ScrollFixLayoutHelper;
import com.alibaba.android.vlayout.layout.SingleLayoutHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * created by YiBeiLiu 2017/04/28
 */

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initRecyclerView();
    }

    private void initRecyclerView() {
        final VirtualLayoutManager layoutManager = new VirtualLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        //设置分割线
        RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(4, 4, 4, 4);
            }
        };
        mRecyclerView.addItemDecoration(itemDecoration);

        //复用池
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        mRecyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);

        //设置 adapter 大集合
        DelegateAdapter delegateAdapter = new DelegateAdapter(layoutManager, true);
        mRecyclerView.setAdapter(delegateAdapter);

        //需要的各种 adapter 集合
        List<DelegateAdapter.Adapter> adapters = new LinkedList<>();
        //5 个线性布局元素
        adapters.add(new MyAdapter(this, new LinearLayoutHelper(), 5));
        //6 个不同样式的网格布局
        GridLayoutHelper glh = new GridLayoutHelper(4);// 4 代表每行被分割成几份
        glh.setSpanSizeLookup(new GridLayoutHelper.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Log.d("asdf", "position = > " + position);
                if (position <= 8) {
                    return 1;//每个元素占 1 份
                } else {
                    return 2;//每个元素占 2 份
                }
            }
        });
        adapters.add(new MyAdapter(this, glh, 6));//总共 6 个元素

        //固定布局，固定在屏幕上不动
        adapters.add(new MyAdapter(this, new FixLayoutHelper(100, 100), 1) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                holder.itemView.setLayoutParams(new VirtualLayoutManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ((ImageView) holder.itemView.findViewById(R.id.iv)).setImageResource(R.mipmap.ic_launcher_round);
            }
        });

        //悬浮布局，可以随意拖动
//        adapters.add(new MyAdapter(this,new FloatLayoutHelper(),1));

        //固定布局，但之后当页面滑动到该图片区域才显示, 可以用来做返回顶部或其他书签等
        ScrollFixLayoutHelper sf = new ScrollFixLayoutHelper(10, 200);//位置
        sf.setShowType(ScrollFixLayoutHelper.SHOW_ON_ENTER);
        adapters.add(new MyAdapter(this, sf, 1) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                ((ImageView) holder.itemView.findViewById(R.id.iv)).setImageResource(R.drawable.top);//箭头
                holder.itemView.findViewById(R.id.iv).setBackgroundColor(Color.WHITE);//白色背景
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRecyclerView.smoothScrollToPosition(0);//快速 go top
                    }
                });
            }
        });

        //栏格布局，在这一排上根据比重调整位置，float 数组总数为 100
        ColumnLayoutHelper clh = new ColumnLayoutHelper();
        clh.setWeights(new float[]{10, 20, 70});
        adapters.add(new MyAdapter(this, clh, 3));

        //通栏布局,只显示一个元素
        adapters.add(new MyAdapter(this, new SingleLayoutHelper(), 10));

        //一拖N布局，可以配置1-5个子元素
        //一个元素
        adapters.add(new MyAdapter(this, new OnePlusNLayoutHelper(), 1));

        //两个元素
        adapters.add(new MyAdapter(this, new OnePlusNLayoutHelper(), 2));

        //3 个元素，2:8 排布
        OnePlusNLayoutHelper op3 = new OnePlusNLayoutHelper();
        op3.setColWeights(new float[]{20f, 80f, 80f});
        adapters.add(new MyAdapter(this, op3, 3));

        //4 个元素 float 数组表示按照从左到右从上到下的顺序遍历元素分配比重，全屏是 100
        OnePlusNLayoutHelper op4 = new OnePlusNLayoutHelper();
        op4.setColWeights(new float[]{50f, 50f, 30f, 20f});
        adapters.add(new MyAdapter(this, op4, 4));

        //5 个元素
        adapters.add(new MyAdapter(this, new OnePlusNLayoutHelper(), 5));

        //5 个元素的 OnePlusNLayoutHelperEx 布局
        adapters.add(new MyAdapter(this, new OnePlusNLayoutHelperEx(), 5));

        //6 个元素的 OnePlusNLayoutHelperEx 布局
        adapters.add(new MyAdapter(this, new OnePlusNLayoutHelperEx(), 6));

        //7 个元素的 OnePlusNLayoutHelperEx 布局
        adapters.add(new MyAdapter(this, new OnePlusNLayoutHelperEx(), 7));

        //重新分配比重的 7 个元素布局
        OnePlusNLayoutHelperEx op = new OnePlusNLayoutHelperEx();
        op.setColWeights(new float[]{20f, 30f, 50f, 20f, 60f, 70f, 10f});
        adapters.add(new MyAdapter(this, op, 7) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                if (getItemViewType(position) == 1) {
                    ImageView iv = (ImageView) holder.itemView.findViewById(R.id.iv);
                    iv.setImageResource(R.mipmap.ic_launcher_round);
                }
            }

            @Override
            public int getItemViewType(int position) {
                if (position == 0) {
                    return 1;
                } else {
                    return 2;
                }
            }
        });

        //将之前设置好的各种 adapter 添加进来
        delegateAdapter.setAdapters(adapters);
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }
}
