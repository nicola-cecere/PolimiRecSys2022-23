<p align="center">
  <img width="100%" src="https://i.imgur.com/tm9mSuM.png" alt="header" />
</p>
# Polimi Recommender System challenge A.Y. 2022/23
This repo contains the code and the data used in [polimi's recsys challenge](https://www.kaggle.com/competitions/recommender-system-2022-challenge-polimi/overview)

## Results
Deadline 2 (final)
- Public leaderboard: 2th
- Private leaderboard: 3th

Deadline 1
- Public leaderboard: 5th
- Private leaderboard: 1st

MAP@10: 0.06200

## Best model
The final model is a hybrid recommender composed of
1. A hybrid recommender trained on a binary URM
2. A hybrid recommender trained on a real-valued URM
3. IALS on binary URM from Implicit's library

The first hybrid is composed of
- SLIM-ElasticNet (stacked URM)
- IALS
- MultVAE
- Mini hybrid of RP3Beta (user-similarity + item-similarity)
- EASE-R (weighted stacked URM)

The second hybrid is composed of
- EASE-R
- SLIM-ElasticNet
- RP3Beta (stacked)
- IALS_implicit
- MultVAE

## This repository
This repository is based on [Maurizio Dacrema's repository](https://github.com/MaurizioFD/RecSys_Course_AT_PoliMi)

## About us
[Andrea Riboni](https://github.com/AndreaRiboni/) and [Nicola Cecere](https://github.com/nicola-cecere)

[My telegram](https://t.me/DatAndre)

## Tuning
- Macbook Pro 2019
- Lenovo Yoga Slim 7 Pro
- Standard E2s v3 (2 vcpus, 16 GiB memory) Azure VM, Ubuntu
