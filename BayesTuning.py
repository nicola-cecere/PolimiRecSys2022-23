import scipy.sparse as sps
import Utils.DataReader as dr
from Utils.confidence_scaling import linear_scaling_confidence

from Evaluation.Evaluator import EvaluatorHoldout
from Recommenders.MatrixFactorization.ImplicitALSRecommender import ImplicitALSRecommender

ICM_tr = dr.load_icm("../data/data_ICM_type_truncated.csv")
URM_all = dr.load_urm("../data/RatingsBinary.csv")
URM_train = sps.load_npz("trainReal.npz")
URM_valid = sps.load_npz("validDefault.npz")
stacked_URM = sps.vstack([URM_all, ICM_tr.T])
stacked_URM_train = sps.vstack([URM_train, ICM_tr.T])

import os
tuning_params = {
    "alpha":(4, 30),
    "factors":(50,220),
    "epochs": (50, 150),
    "regularization": (0.0001, 0.01),
    "icm_coeff": (0.1, 2)
}

evaluator_validation = EvaluatorHoldout(URM_valid, cutoff_list=[10])

recommender = {}
def BO_func( factors,
             epochs,
             alpha,
             regularization,
             icm_coeff
             ):
    recommender = ImplicitALSRecommender(URM_train, ICM_tr)
    recommender.fit(factors=int(factors),
                    regularization= regularization,
                    use_gpu=False,
                    iterations=int(epochs),
                    num_threads=0,
                    icm_coeff=icm_coeff,
                    confidence_scaling=linear_scaling_confidence,
                    **{"alpha":alpha}
                    )
    result_dict, _ = evaluator_validation.evaluateRecommender(recommender)

    return result_dict["MAP"][10]

from bayes_opt import BayesianOptimization

optimizer = BayesianOptimization(
    f=BO_func,
    pbounds=tuning_params,
    verbose=5,
    random_state=42,
)

from bayes_opt.logger import JSONLogger
from bayes_opt.event import Events

logger = JSONLogger(path="/home/azureuser/Downloads/RecSys_Course_AT_PoliMi-master/logs/" + "IALS" + "_logs.json")
optimizer.subscribe(Events.OPTIMIZATION_STEP, logger)

optimizer.maximize(
    init_points=60,
    n_iter=300
)

hyperparameters = optimizer.max['params']