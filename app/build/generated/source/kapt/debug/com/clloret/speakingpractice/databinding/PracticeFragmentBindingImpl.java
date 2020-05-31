package com.clloret.speakingpractice.databinding;
import com.clloret.speakingpractice.R;
import com.clloret.speakingpractice.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class PracticeFragmentBindingImpl extends PracticeFragmentBinding implements com.clloret.speakingpractice.generated.callback.OnClickListener.Listener {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.exerciseResultView, 8);
        sViewsWithIds.put(R.id.fabRecognizeSpeech, 9);
        sViewsWithIds.put(R.id.resultsSeparator, 10);
    }
    // views
    // variables
    @Nullable
    private final android.view.View.OnClickListener mCallback2;
    @Nullable
    private final android.view.View.OnClickListener mCallback3;
    @Nullable
    private final android.view.View.OnClickListener mCallback1;
    // values
    // listeners
    // Inverse Binding Event Handlers

    public PracticeFragmentBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 11, sIncludes, sViewsWithIds));
    }
    private PracticeFragmentBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 2
            , (android.widget.Button) bindings[5]
            , (android.widget.Button) bindings[4]
            , (android.widget.ImageView) bindings[8]
            , (com.google.android.material.floatingactionbutton.FloatingActionButton) bindings[9]
            , (com.google.android.material.floatingactionbutton.FloatingActionButton) bindings[2]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[0]
            , (android.widget.TextView) bindings[1]
            , (android.widget.TextView) bindings[6]
            , (android.widget.TextView) bindings[7]
            , (android.widget.TextView) bindings[10]
            , (android.widget.TextView) bindings[3]
            );
        this.btnNext.setTag(null);
        this.btnPrevious.setTag(null);
        this.fabTextToSpeech.setTag(null);
        this.linearLayout.setTag(null);
        this.practicePhrase.setTag(null);
        this.resultsCorrect.setTag(null);
        this.resultsIncorrect.setTag(null);
        this.translatedPhrase.setTag(null);
        setRootTag(root);
        // listeners
        mCallback2 = new com.clloret.speakingpractice.generated.callback.OnClickListener(this, 2);
        mCallback3 = new com.clloret.speakingpractice.generated.callback.OnClickListener(this, 3);
        mCallback1 = new com.clloret.speakingpractice.generated.callback.OnClickListener(this, 1);
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x8L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
        if (BR.model == variableId) {
            setModel((com.clloret.speakingpractice.exercise.PracticeViewModel) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setModel(@Nullable com.clloret.speakingpractice.exercise.PracticeViewModel Model) {
        this.mModel = Model;
        synchronized(this) {
            mDirtyFlags |= 0x4L;
        }
        notifyPropertyChanged(BR.model);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeModelCurrentExercise((androidx.lifecycle.LiveData<com.clloret.speakingpractice.domain.entities.Exercise>) object, fieldId);
            case 1 :
                return onChangeModelResultValues((androidx.lifecycle.LiveData<com.clloret.speakingpractice.domain.entities.ExerciseResultTuple>) object, fieldId);
        }
        return false;
    }
    private boolean onChangeModelCurrentExercise(androidx.lifecycle.LiveData<com.clloret.speakingpractice.domain.entities.Exercise> ModelCurrentExercise, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeModelResultValues(androidx.lifecycle.LiveData<com.clloret.speakingpractice.domain.entities.ExerciseResultTuple> ModelResultValues, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x2L;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        com.clloret.speakingpractice.exercise.PracticeViewModel model = mModel;
        int modelResultValuesCorrect = 0;
        com.clloret.speakingpractice.domain.entities.Exercise modelCurrentExerciseGetValue = null;
        com.clloret.speakingpractice.domain.entities.ExerciseResultTuple modelResultValuesGetValue = null;
        java.lang.String modelCurrentExercisePracticePhrase = null;
        int modelResultValuesIncorrect = 0;
        java.lang.String integerToStringModelResultValuesIncorrect = null;
        androidx.lifecycle.LiveData<com.clloret.speakingpractice.domain.entities.Exercise> modelCurrentExercise = null;
        java.lang.String modelCurrentExerciseTranslatedPhrase = null;
        java.lang.String integerToStringModelResultValuesCorrect = null;
        androidx.lifecycle.LiveData<com.clloret.speakingpractice.domain.entities.ExerciseResultTuple> modelResultValues = null;

        if ((dirtyFlags & 0xfL) != 0) {


            if ((dirtyFlags & 0xdL) != 0) {

                    if (model != null) {
                        // read model.currentExercise
                        modelCurrentExercise = model.getCurrentExercise();
                    }
                    updateLiveDataRegistration(0, modelCurrentExercise);


                    if (modelCurrentExercise != null) {
                        // read model.currentExercise.getValue()
                        modelCurrentExerciseGetValue = modelCurrentExercise.getValue();
                    }


                    if (modelCurrentExerciseGetValue != null) {
                        // read model.currentExercise.getValue().practicePhrase
                        modelCurrentExercisePracticePhrase = modelCurrentExerciseGetValue.getPracticePhrase();
                        // read model.currentExercise.getValue().translatedPhrase
                        modelCurrentExerciseTranslatedPhrase = modelCurrentExerciseGetValue.getTranslatedPhrase();
                    }
            }
            if ((dirtyFlags & 0xeL) != 0) {

                    if (model != null) {
                        // read model.resultValues
                        modelResultValues = model.getResultValues();
                    }
                    updateLiveDataRegistration(1, modelResultValues);


                    if (modelResultValues != null) {
                        // read model.resultValues.getValue()
                        modelResultValuesGetValue = modelResultValues.getValue();
                    }


                    if (modelResultValuesGetValue != null) {
                        // read model.resultValues.getValue().correct
                        modelResultValuesCorrect = modelResultValuesGetValue.getCorrect();
                        // read model.resultValues.getValue().incorrect
                        modelResultValuesIncorrect = modelResultValuesGetValue.getIncorrect();
                    }


                    // read Integer.toString(model.resultValues.getValue().correct)
                    integerToStringModelResultValuesCorrect = java.lang.Integer.toString(modelResultValuesCorrect);
                    // read Integer.toString(model.resultValues.getValue().incorrect)
                    integerToStringModelResultValuesIncorrect = java.lang.Integer.toString(modelResultValuesIncorrect);
            }
        }
        // batch finished
        if ((dirtyFlags & 0x8L) != 0) {
            // api target 1

            this.btnNext.setOnClickListener(mCallback3);
            this.btnPrevious.setOnClickListener(mCallback2);
            this.fabTextToSpeech.setOnClickListener(mCallback1);
        }
        if ((dirtyFlags & 0xdL) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.practicePhrase, modelCurrentExercisePracticePhrase);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.translatedPhrase, modelCurrentExerciseTranslatedPhrase);
        }
        if ((dirtyFlags & 0xeL) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.resultsCorrect, integerToStringModelResultValuesCorrect);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.resultsIncorrect, integerToStringModelResultValuesIncorrect);
        }
    }
    // Listener Stub Implementations
    // callback impls
    public final void _internalCallbackOnClick(int sourceId , android.view.View callbackArg_0) {
        switch(sourceId) {
            case 2: {
                // localize variables for thread safety
                // model
                com.clloret.speakingpractice.exercise.PracticeViewModel model = mModel;
                // model != null
                boolean modelJavaLangObjectNull = false;



                modelJavaLangObjectNull = (model) != (null);
                if (modelJavaLangObjectNull) {


                    model.loadPreviousExercise();
                }
                break;
            }
            case 3: {
                // localize variables for thread safety
                // model
                com.clloret.speakingpractice.exercise.PracticeViewModel model = mModel;
                // model != null
                boolean modelJavaLangObjectNull = false;



                modelJavaLangObjectNull = (model) != (null);
                if (modelJavaLangObjectNull) {


                    model.loadNextExercise();
                }
                break;
            }
            case 1: {
                // localize variables for thread safety
                // model
                com.clloret.speakingpractice.exercise.PracticeViewModel model = mModel;
                // model != null
                boolean modelJavaLangObjectNull = false;



                modelJavaLangObjectNull = (model) != (null);
                if (modelJavaLangObjectNull) {


                    model.speakText();
                }
                break;
            }
        }
    }
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): model.currentExercise
        flag 1 (0x2L): model.resultValues
        flag 2 (0x3L): model
        flag 3 (0x4L): null
    flag mapping end*/
    //end
}