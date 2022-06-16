package fr.insalyon.mxyns.icrc.dna.data_gathering.input;

import android.content.res.Resources;
import android.util.Log;

import java.util.Map;

import fr.insalyon.mxyns.icrc.dna.MainActivity;
import fr.insalyon.mxyns.icrc.dna.utils.XmlUtils;

public abstract class StringListTemplateFragment extends ListTemplateFragment<String> {

    public static class ForList extends StringListTemplateFragment {
        @Override
        protected String[] getValuesArray() {
            return getResources().getStringArray(getResources().getIdentifier(this.input_name + "_values", "array", MainActivity.class.getPackage().getName()));
        }
    }

    // saved value (result of the input) is the internal name
    public static class ForMap extends StringListTemplateFragment {

        // contains mapping : internal_name -> display_name
        Map<String, String> map = null;

        private Map<String, String> map() {

            if (this.map == null) {

                String mapNameRef = this.input_name + "_map";
                Resources res = getResources();
                this.map = XmlUtils.getHashMapResource(res, res.getIdentifier(mapNameRef, "xml", MainActivity.class.getPackage().getName()));
                Log.d("xml-map", "" + map);
            }

            return this.map;
        }

        @Override
        protected String[] getValuesArray() {
            return map().keySet().toArray(new String[0]);
        }

        @Override
        protected String mapToDisplay(ListOption<String> input) {
            return map().get(input.value);
        }
    }

}
