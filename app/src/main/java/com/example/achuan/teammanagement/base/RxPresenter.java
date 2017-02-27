package com.example.achuan.teammanagement.base;

/**
 * Created by achuan 2016/10/29.
 * //基于Rx的Presenter封装,控制订阅的生命周期
 * 功能：当前还未使用Rx
 */
public class RxPresenter<T extends BaseView> implements BasePresenter<T> {

    protected T mView;//view接口类型的弱引用
    /*protected CompositeSubscription mCompositeSubscription;

    protected void unSubscribe() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }

    protected void addSubscrebe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }*/


    @Override
    public void attachView(T view) {
        this.mView = view;
        //mView=new WeakReference<T>(view);//建立关联
    }
    @Override
    public void detachView() {
        this.mView = null;
        //unSubscribe();
        /*if(mView!=null){
            mView.clear();//取消关联
            mView=null;
        }*/
    }
}
