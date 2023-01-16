from Recommenders.BaseRecommender import BaseRecommender
import pandas as pd
import numpy as np
import scipy.sparse as sps


class SpecializedRecommender(BaseRecommender):
    RECOMMENDER_NAME = "SpecializedRecommender"

    def __init__(self, URM_train, recommenderStandard, recommenderCold, verbose=True):
        super(SpecializedRecommender, self).__init__(URM_train, verbose=verbose)
        self.recommenderStandard = recommenderStandard
        self.recommenderCold = recommenderCold

    def fit(self, user_in_cold):
        self.user_in_cold = user_in_cold
        print("cold users:")
        print(user_in_cold)

    def recommend(self, user_id_array, cutoff = None, remove_seen_flag=True, items_to_compute = None,
                  remove_top_pop_flag = False, remove_custom_items_flag = False, return_scores = False):
        print("recommending items to users: ", user_id_array)
        if isinstance(user_id_array, int):
            nof_recommendations = 1
            user_id_array = [user_id_array]
        else:
            nof_recommendations = len(user_id_array)
        recommendations = [None] * nof_recommendations
        scores = [None] * nof_recommendations

        chosen_recommender = self.recommenderStandard

        for user_index in range(nof_recommendations):
            user_id = user_id_array[user_index]

            if user_id in self.user_in_cold:
                print("using the cold recommender")
                chosen_recommender = self.recommenderCold
            else:
                print("using the generic recommender")

            if(return_scores):
                rec, score = chosen_recommender.recommend(
                    user_id_array=user_id,
                    cutoff=cutoff,
                    remove_seen_flag=remove_seen_flag,
                    items_to_compute=items_to_compute,
                    remove_top_pop_flag=remove_top_pop_flag,
                    remove_custom_items_flag=remove_custom_items_flag,
                    return_scores=return_scores
                )
                recommendations[user_index] = rec
                scores[user_index] = score
            else:
                rec = chosen_recommender.recommend(
                    user_id_array=user_id,
                    cutoff=cutoff,
                    remove_seen_flag=remove_seen_flag,
                    items_to_compute=items_to_compute,
                    remove_top_pop_flag=remove_top_pop_flag,
                    remove_custom_items_flag=remove_custom_items_flag,
                    return_scores=return_scores
                )
                recommendations[user_index] = rec
                print("recommendations: ")
                print(rec)
        if return_scores:
            return recommendations, np.asarray(scores).reshape(nof_recommendations, self.n_items)
        else:
            return recommendations

class MultiSpecializedRecommender(BaseRecommender):
    RECOMMENDER_NAME = "MultiSpecializedRecommender"

    def __init__(self, URM_train, recommender_cold, recommender_warm, recommender_generic, verbose=True):
        super(MultiSpecializedRecommender, self).__init__(URM_train, verbose=verbose)
        self.recommender_generic = recommender_generic
        self.recommender_warm = recommender_warm
        self.recommender_cold = recommender_cold
        self.user_popularity = np.ediff1d(sps.csr_matrix(URM_train).indptr)
        self.low_threshold = self.recommender_cold.getThreshold()
        self.high_threshold = self.recommender_warm.getThreshold()

    def _compute_item_score(self, user_id_array, items_to_compute=None):
        item_weights_low = self.recommender_1._compute_item_score(user_id_array)
        item_weights_medium = self.recommender_2._compute_item_score(user_id_array)
        item_weights_high = self.recommender_3._compute_item_score(user_id_array)

        item_weights = item_weights_medium
        for user_index in range(len(user_id_array)):
            user_id = user_id_array[user_index]
            user_int = self.user_popularity[user_id]
            if user_int > self.high_threshold:
                item_weights[user_index] = item_weights_high
            elif user_int < self.low_threshold:
                item_weights[user_index] = item_weights_low
