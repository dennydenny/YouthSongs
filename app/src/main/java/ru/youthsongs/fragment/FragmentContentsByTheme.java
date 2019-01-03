package ru.youthsongs.fragment;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.youthsongs.R;


public class FragmentContentsByTheme extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fraglayout_contents_bytheme, null);

        // TODO: Migrate all arrays to string resourses

        String[] groups = new String[] {"Бог", "Будущее", "Евангелие", "Иисус Христос", "Святой Дух", "Слово Божие", "Христианская жизнь", "Церковь"};

        String[] God = new String[] {"Благодать и милость Бога", "Верность и неизменность Бога", "Забота Бога", "Замысел Бога", "Качества Бога",
                "Любовь Бога", "Превосходство Бога", "Святость Бога", "Слава и величие Бога", "Творец", "Троица", "Царство Бога"};
        String[] Future = new String[] {"Второе пришествие Христа", "Небеса"};
        String[] Gospel = new String[] {"Искупление", "Оправдание", "Прощение", "Спасение", "Усыновление"};
        String[] Jesus = new String[] {"Воскресение Христа", "Господство Христа", "Заместительная жертва Христа", "Крест Христа", "Любовь Христа",
                "Первосвященство Христа", "Превосходство Христа", "Рождество Христа", "Слава и величие Христа", "Смирение Христа", "Спаситель",
                "Страдания Христа", "Ходатайство Христа", "Христос – путь", "Царство Христа"};
        String[] Spirit = new String[] {"Озарение Святого Духа", "Присутствие Святого Духа", "Святой Дух"};
        String[] Word = new String[] {"Слово Божие"};
        String[] Life = new String[] {"Благовестие", "Благодарность Богу", "Вера", "Грех", "Духовная война", "Жажда по Богу", "Зависимость от Бога",
                "Испытания и скорби", "Любовь к Богу", "Мир и покой в Боге", "Молитва", "Надежда и упование на Бога", "Освящение", "Познание Бога",
                "Покаяние и исповедание", "Посвящённость и служение Богу", "Прославление Бога", "Радость и счастье в Боге", "Смирение",
                "Стойкость верующих", "Суетность мира"};
        //String[] Church = new String[] {"Единство верующих", "Причастие", "Церковь"};
        String[] Church = getResources().getStringArray(R.array.Theme_Church);
        String[] [] Themes = new String[] [] {God, Future, Gospel, Jesus, Spirit, Word, Life, Church };
        ArrayList<Map<String, String>> groupData;

        // коллекция для элементов одной группы
        ArrayList<Map<String, String>> childDataItem;

        // общая коллекция для коллекций элементов
        ArrayList<ArrayList<Map<String, String>>> childData;
        // в итоге получится childData = ArrayList<childDataItem>

        // список аттрибутов группы или элемента
        Map<String, String> m;

        // заполняем коллекцию групп из массива с названиями групп
        groupData = new ArrayList<Map<String, String>>();
        for (String group : groups) {
            // заполняем список аттрибутов для каждой группы
            m = new HashMap<String, String>();
            m.put("groupName", group); // имя компании
            groupData.add(m);
        }

        // список аттрибутов групп для чтения
        String groupFrom[] = new String[] {"groupName"};
        // список ID view-элементов, в которые будет помещены аттрибуты групп
        int groupTo[] = new int[] {R.id.theme_item_v2};

        // создаем коллекцию для коллекций элементов
        childData = new ArrayList<ArrayList<Map<String, String>>>();


        /*for (int i = 0; i < Themes.length; i++) {
            Log.i("Themes", "Spans num " + i + " is " + Themes [i].length);
            Log.i("Themes", "Test is  " + Themes [i] [0]);
        }
        */

        for (int i = 0; i < Themes.length; i++) {
            childDataItem = new ArrayList<Map<String, String>>();
            for (int j = 0; j < Themes[i].length; j++) {
                m = new HashMap<String, String>();
                m.put("subtheme", Themes[i][j]); // название телефона
                childDataItem.add(m);
            }
            childData.add(childDataItem);
        }

        // список аттрибутов элементов для чтения
        String childFrom[] = new String[] {"subtheme"};
        // список ID view-элементов, в которые будет помещены аттрибуты элементов
        int childTo[] = new int[] {android.R.id.text1};

        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                getActivity(),
                groupData,
                R.layout.theme_item_title_v2,
                groupFrom,
                groupTo,
                childData,
                android.R.layout.simple_list_item_1,
                childFrom,
                childTo);

        ExpandableListView elvMain;
        elvMain = (ExpandableListView) v.findViewById(R.id.elvMain);
        elvMain.setAdapter(adapter);
        elvMain.setGroupIndicator(null);

        // Expand all the groups
        for (int position = 1; position <= adapter.getGroupCount(); position++)
         elvMain.expandGroup(position - 1);

        elvMain.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                TextView tv = (TextView) v;
                String selected_theme = tv.getText().toString();
                Log.i("Themes", "You select a " + selected_theme);
                FragmentContentByThemeList frag2 = new FragmentContentByThemeList();

                Bundle bundle = new Bundle();
                bundle.putString("selected_theme", selected_theme);
                frag2.setArguments(bundle);

                android.support.v4.app.FragmentTransaction ftrans = getFragmentManager().beginTransaction();
                ftrans.replace(R.id.contents_by_theme_frame, frag2);
                ftrans.addToBackStack(null);
                ftrans.commit();

                return false;
            }
        });

        elvMain.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return true;
            }
        });

        return v;
    }
}
