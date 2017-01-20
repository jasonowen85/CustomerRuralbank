package com.grgbanking.ruralbank.main.fragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class BaseFragment extends Fragment implements OnClickListener,BaseFragmentInterface{
	protected LayoutInflater mInflater;
	private int workorderid;
	public int getWorkorderId() {
		return workorderid;
	}

	public void setWorkorderId(int workorderid) {
		this.workorderid = workorderid;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	protected int getLayoutId() {
		return 0;
	}
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public void initView(View view) {

		
	}

	@Override
	public void initData() {

		
	}

	@Override
	public void onClick(View v) {

		
	}
	protected View inflateView(int resId) {
		return this.mInflater.inflate(resId, null);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.mInflater = inflater;
		View view = super.onCreateView(inflater, container, savedInstanceState);
		return view;
	}
	/*protected void loadFragment(Fragment fragment) {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fragment_my, fragment);
		ft.addToBackStack("tag");
		ft.commit();
	}
	protected void loadFragment(Fragment fragment,Bundle bundle) {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		fragment.setArguments(bundle);
		ft.replace(R.id.fragment_my, fragment);
		ft.addToBackStack("tag");
		ft.commit();
	}*/

	public boolean onCreateOptionsMenu(Menu menu) {

		return false;
	}
}
