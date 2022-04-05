package io.github.oldmanpushcart.hexapod.api;

/**
 * 关节序列
 */
public enum Joint {

    L_F_H, L_F_K, L_F_A, /*--左前--|+|--右前--*/ R_F_H, R_F_K, R_F_A,

    L_M_H, L_M_K, L_M_A, /*--左中--|+|--右中--*/ R_M_H, R_M_K, R_M_A,

    L_H_H, L_H_K, L_H_A, /*--左后--|+|--右后--*/ R_H_H, R_H_K, R_H_A

}
