package cn.edu.nnnu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.clans.fab.FloatingActionButton;
import com.kelin.scrollablepanel.library.ScrollablePanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.edu.tools.ACache;
import cn.edu.tools.ListData;
import cn.edu.tools.MapDataIntent;
import cn.edu.tools.ScrollPanelAdapter;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ACache aCache;
    private ScrollablePanel scrollablePanel;
    private ScrollPanelAdapter adapter;
    private List<List<String>> lists;
    private Map<String, String> cookies;
    private DrawerLayout drawerLayout;

    private Spinner spinner;
    private ArrayAdapter<String> arrayAdapter;

    private WebView webView;

    private List<String> weeks = new ArrayList<>();
    private FloatingActionButton btnAddCourse,btnCourseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //得到缓存
        aCache = ACache.get(this);
        baseInit();

        //得到列表大小
        if (aCache.getAsString("list_size") != null) {
            init();
        }

    }

    //重新加载数据
    @Override
    protected void onRestart() {
        super.onRestart();
        if (aCache.getAsString("list_size") != null) {
            init();
        }
    }

    private void baseInit() {
        toolbar = findViewById(R.id.toolbar);
        webView = findViewById(R.id.webview);
        btnAddCourse = findViewById(R.id.menu_import_course);
        btnCourseList = findViewById(R.id.menu_course_list);
        scrollablePanel = findViewById(R.id.scrollable_panel);
        drawerLayout = findViewById(R.id.drawer);
        setSupportActionBar(toolbar);
        setTitle("首页");
        toolbar.setNavigationIcon(R.drawable.ic_humanres);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);

        btnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void init(){
        //初始化Spinner
        initSpinner();
        initData(1);
        //初始化views
        initViews();
        //获得cookies
        cookies = ((MapDataIntent) aCache.getAsObject("cookie")).getMap();
    }

    private void initSpinner() {
        spinner = findViewById(R.id.spinner);
        weeks.clear();
        for (int i = 0; i < Integer.parseInt(aCache.getAsString("list_size")); i++) {
            weeks.add("第" + (i + 1) + "周");
        }
        if(arrayAdapter == null)
            arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, weeks);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initData(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initViews() {
        scrollablePanel.setPanelAdapter(adapter);
    }


    //解析数据
    private void initData(int position) {
        lists = null;
        lists = new ArrayList<>();
        ListData<List<String>> listListData = (ListData<List<String>>) aCache.getAsObject("week" + position);
        lists = listListData.getStrings();
        int bigSize = 0;
        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).size() >= lists.get(bigSize).size()) {
                bigSize = i;
            }
        }

        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).size() < lists.get(bigSize).size()) {
                for (int k = lists.get(bigSize).size() - lists.get(i).size(); k > 0; k--) {
                    lists.get(i).add("");
                }
            }
        }

        lists.remove(lists.size() - 1);

        for (int i = 0; i < lists.size(); i++) {
            if (i != 0)
                lists.get(i).set(0, "第" + i + "节");
        }

        for (int i = 0; i < lists.get(0).size(); i++) {
            String str = "";
            switch (i) {
                case 0:
                    str = "周/节";
                    break;
                case 1:
                    str = "星期一";
                    break;
                case 2:
                    str = "星期二";
                    break;
                case 3:
                    str = "星期三";
                    break;
                case 4:
                    str = "星期四";
                    break;
                case 5:
                    str = "星期五";
                    break;
                case 6:
                    str = "星期六";
                    break;
                case 7:
                    str = "星期日";
                    break;
            }
            lists.get(0).set(i, str);
            if (adapter == null) {
                adapter = new ScrollPanelAdapter(this);
            }
            adapter.setLists(lists);

            scrollablePanel.notifyDataSetChanged();

        }
    }
}