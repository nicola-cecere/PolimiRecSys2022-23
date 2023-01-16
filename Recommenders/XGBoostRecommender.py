from numpy import linalg as LA
from Recommenders.BaseRecommender import BaseRecommender
import pandas as pd
import Utils.DataReader as dr
import scipy.sparse as sps
from tqdm import tqdm
from xgboost import XGBRanker
import numpy as np
from numpy import savetxt
import os


class XGBoostRecommender(BaseRecommender):

    RECOMMENDER_NAME = "DifferentLossScoresHybridRecommender"


    def __init__(self, URM_train, dataset_path, verbose = False):
        super(XGBoostRecommender, self).__init__(URM_train, verbose)
        self.training_dataframe = pd.read_csv(dataset_path)

    def setSubmissionPath(self, path):
        self.submission = pd.read_csv(path)

    def fit(self,
            n_estimators = 50,
            learning_rate = 1e-1,
            reg_alpha = 1e-1,
            reg_lambda = 1e-1,
            max_depth = 5,
            max_leaves = 0,
            grow_policy = "depthwise",
            objective = "pairwise",
            booster = "gbtree",
            use_user_profile = False,
            random_seed = None):
        self.n_estimators = n_estimators
        self.learning_rate = learning_rate
        self.reg_alpha = reg_alpha
        self.reg_lambda = reg_lambda
        self.max_depth = max_depth
        self.max_leaves = max_leaves
        self.grow_policy = grow_policy
        self.objective = objective
        self.booster = booster
        self.use_user_profile = use_user_profile
        self.random_seed = random_seed

        #fit
        self.XGB_model = XGBRanker(
                    objective='rank:{}'.format(self.objective),
                    n_estimators = int(self.n_estimators),
                    random_state = self.random_seed,
                    learning_rate = self.learning_rate,
                    reg_alpha = self.reg_alpha,
                    reg_lambda = self.reg_lambda,
                    max_depth = int(self.max_depth),
                    max_leaves = int(self.max_leaves),
                    grow_policy = self.grow_policy,
                    verbosity = 0, # 2 if self.verbose else 0,
                    booster = self.booster,
                    )

        y_train = self.training_dataframe["Label"]
        X_train = self.training_dataframe.drop(columns=["Label"])
        groups = self.training_dataframe.groupby("UserID").size().values

        self.XGB_model.fit(X_train,
                      y_train,
                      group=groups,
                      verbose=True)

        #predict
        predicts = self.XGB_model.predict(X_train)

        #save predictions
        savetxt('C:\\Users\\Andrea\\AppData\\Roaming\\JetBrains\\DataSpell2022.2\\projects\\RecSys2022\\data\\xgboost\\predicts.csv', predicts, delimiter=',')

        #run java to sort predictions
        result = os.system('java -jar URM_creator.jar Sort_XGBoost') #non requested users too
        print("java returned ", result)

        #save submission
        self.setSubmissionPath('C:\\Users\\Andrea\\AppData\\Roaming\\JetBrains\\DataSpell2022.2\\projects\\RecSys2022\\data\\xgboost\\sub.csv')

    def _compute_item_score(self, user_id_array, items_to_compute = None):
        """

        :param user_id_array:       array containing the user indices whose recommendations need to be computed
        :param items_to_compute:    array containing the items whose scores are to be computed.
                                        If None, all items are computed, otherwise discarded items will have as score -np.inf
        :return:                    array (len(user_id_array), n_items) with the score.
        """
        #print("user_id_array", user_id_array)
        item_scores = - np.ones((len(user_id_array), self.n_items), dtype=np.float32)*np.inf
        #print("item scores")
        #print(item_scores)
        for index in range(len(user_id_array)):
            #get their recommendations
            items = self.submission[self.submission["user_id"] == user_id_array[index]].item_list.values[0].split(" ")
            for item_index, item in enumerate(items):
                item_scores[index, int(item)] = (len(items) - item_index) / len(items)
                #print("item index: {}, item: {}, score: {}".format(item_index, item, item_scores[index, int(item)]))

        return item_scores