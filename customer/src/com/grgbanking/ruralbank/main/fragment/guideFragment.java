package com.grgbanking.ruralbank.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.grgbanking.ruralbank.DemoCache;
import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.login.LoginActivity;
import com.grgbanking.ruralbank.main.activity.MainActivity;
import com.grgbanking.ruralbank.common.util.fragmentanimations.CubeAnimation;
import com.grgbanking.ruralbank.common.util.fragmentanimations.FlipAnimation;
import com.grgbanking.ruralbank.common.util.fragmentanimations.MoveAnimation;
import com.grgbanking.ruralbank.common.util.fragmentanimations.PushPullAnimation;
import com.grgbanking.ruralbank.common.util.fragmentanimations.SidesAnimation;

/**
 * @author kakajika
 * @since 2015/11/27
 */
public class guideFragment extends Fragment implements View.OnClickListener {

    FrameLayout ll_guide;
    private static int index;

    @Override
    public void onClick(View v) {
        switch (index) {
            case 1:
                getArguments().putInt("direction", LEFT);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.layout_guide, guideFragment.newInstance(LEFT));
                ft.commit();
                break;
            case 2:
                if (DemoCache.getUserid() == null) {
                    startActivity(new Intent(getActivity().getBaseContext(), LoginActivity.class));
                    getActivity().finish();
                } else {
                    startActivity(new Intent(getActivity().getBaseContext(), MainActivity.class));
                    getActivity().finish();
                }
                break;
            default:
                getArguments().putInt("direction", LEFT);
                FragmentTransaction ft1 = getFragmentManager().beginTransaction();
                ft1.replace(R.id.layout_guide, guideFragment.newInstance(LEFT));
                ft1.commit();
                break;
        }

    }

    @IntDef({NONE, MOVE, CUBE, FLIP, PUSHPULL, SIDES, CUBEMOVE, MOVECUBE, PUSHMOVE, MOVEPULL, FLIPMOVE, MOVEFLIP, FLIPCUBE, CUBEFLIP})
    public @interface AnimationStyle {
    }

    public static final int NONE = 0;
    public static final int MOVE = 1;
    public static final int CUBE = 2;
    public static final int FLIP = 3;
    public static final int PUSHPULL = 4;
    public static final int SIDES = 5;
    public static final int CUBEMOVE = 6;
    public static final int MOVECUBE = 7;
    public static final int PUSHMOVE = 8;
    public static final int MOVEPULL = 9;
    public static final int FLIPMOVE = 10;
    public static final int MOVEFLIP = 11;
    public static final int FLIPCUBE = 12;
    public static final int CUBEFLIP = 13;

    @IntDef({NODIR, UP, DOWN, LEFT, RIGHT})
    public @interface AnimationDirection {
    }

    public static final int NODIR = 0;
    public static final int UP = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;
    public static final int RIGHT = 4;

    private static final long DURATION = 500;

    @AnimationStyle
    private static int sAnimationStyle = SIDES;

    public static guideFragment newInstance(@AnimationDirection int direction) {
        guideFragment f = new guideFragment();
        f.setArguments(new Bundle());
        f.getArguments().putInt("direction", direction);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_anim, container, false);
        index++;
        switch (index) {
            case 1:
                view.setBackgroundResource(R.drawable.tip1);
                break;
            case 2:
                view.setBackgroundResource(R.drawable.tip2);
                break;
            default:
                view.setBackgroundResource(R.drawable.tip1);
                break;
        }

        ll_guide = (FrameLayout) view.findViewById(R.id.fl_content);
        ll_guide.setOnClickListener(this);
        return view;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        switch (sAnimationStyle) {
            case MOVE:
                return MoveAnimation.create(MoveAnimation.LEFT, enter, DURATION);
            case CUBE:
                return CubeAnimation.create(CubeAnimation.LEFT, enter, DURATION);
            case FLIP:
                return FlipAnimation.create(FlipAnimation.LEFT, enter, DURATION);
            case PUSHPULL:
                return PushPullAnimation.create(PushPullAnimation.LEFT, enter, DURATION);
            case SIDES:
                return SidesAnimation.create(SidesAnimation.LEFT, enter, DURATION);
            case CUBEMOVE:
                return enter ? MoveAnimation.create(MoveAnimation.LEFT, enter, DURATION).fading(0.3f, 1.0f) :
                        CubeAnimation.create(CubeAnimation.LEFT, enter, DURATION).fading(1.0f, 0.3f);
            case MOVECUBE:
                return enter ? CubeAnimation.create(CubeAnimation.LEFT, enter, DURATION).fading(0.3f, 1.0f) :
                        MoveAnimation.create(MoveAnimation.LEFT, enter, DURATION).fading(1.0f, 0.3f);
            case PUSHMOVE:
                return enter ? MoveAnimation.create(MoveAnimation.LEFT, enter, DURATION) :
                        PushPullAnimation.create(PushPullAnimation.LEFT, enter, DURATION);
            case MOVEPULL:
                return enter ? PushPullAnimation.create(PushPullAnimation.LEFT, enter, DURATION) :
                        MoveAnimation.create(MoveAnimation.LEFT, enter, DURATION).fading(1.0f, 0.3f);
            case FLIPMOVE:
                return enter ? MoveAnimation.create(MoveAnimation.LEFT, enter, DURATION) :
                        FlipAnimation.create(FlipAnimation.LEFT, enter, DURATION);
            case MOVEFLIP:
                return enter ? FlipAnimation.create(FlipAnimation.LEFT, enter, DURATION) :
                        MoveAnimation.create(MoveAnimation.LEFT, enter, DURATION).fading(1.0f, 0.3f);
            case FLIPCUBE:
                return enter ? CubeAnimation.create(CubeAnimation.LEFT, enter, DURATION) :
                        FlipAnimation.create(FlipAnimation.LEFT, enter, DURATION);
            case CUBEFLIP:
                return enter ? FlipAnimation.create(FlipAnimation.LEFT, enter, DURATION) :
                        CubeAnimation.create(CubeAnimation.LEFT, enter, DURATION).fading(1.0f, 0.3f);
            case NONE:
                break;
        }
        return null;
    }


}
