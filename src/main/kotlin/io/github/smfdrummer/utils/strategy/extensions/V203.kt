@file:Suppress("unused", "FunctionName")

package io.github.smfdrummer.utils.strategy.extensions

import io.github.smfdrummer.utils.strategy.StrategyBuilder

fun StrategyBuilder.V203() = packet {
    i = "V203"
    retry = 4

    parse(
        """
      {
        "_id": 203,
        "c": "0",
        "cl": [],
        "dcl": {},
        "dl": [],
        "g": "0",
        "m": "9d8fe592ba17620784befca768f69e23",
        "n": "活泼的窝瓜",
        "pcl": {},
        "pl": [
          1001,
          1002,
          1003,
          1004
        ],
        "pr": "H4sIAAAAAAAAE21YXXuiPhb_Qv8LCLW7vSyjgu6YrrQVkzshnQEkGZ9iRfj0exLIW2cvfExIcnLez-_kY9iO9Mfm8WPYDuWwedy9lSMe9_1u_B3ufvR1UW8eN-09xHw_EvWPOQnhfyQPeA3_aLWg8n_MzmTYdBu-q1-aVY-b_QLXfc3Qddw0f-67JQle3gisUw77Hje13HsPyTE-w3q9gb00kWefu404DPLePAxgz6Jl0bYhirdnhJdn9PPH9oulE79qj8iC8vu96bbx7hX0rPa0VwbnEU2YojmfH0j0rNeuf5_NJI_X3Svo4_htjYdmjfD1sGkunOT3kcp5fg_02imNmxL0TPm6K9E7nGMDQVnt7qcgJ016zcefUsQevRLdB8mzpZGNZXKvpf0mOdYhzSV_l-Io9x8PLYE9ek59HQo1Bxt_CK13uN-jT2vJM-gjVPzxbU9qOy8E65kn0_r81zfh26VMgWefj07bn6FAyx4V-eFs5JDyjatuV_fjz-VqlN8Y8LFrSqCPv9lq2_v2ud_KiF7knZrvEi1ClrfK7_bjqt81-yFrNteXt98Pu9cgwAFd_Xz7jV7eV9fdaoXwO4myt_VqLz1c-ikims9A8iZ1SI9ZT16NP_XSf-fvX2Wy0vu_qN7Dr49aPhbhTyNrujG0P_Jz_SI60EuodFTwJ3leyjRKHf98W8n4mMfrJzgDPn7W5ys1hvPkKHUy0ZfjUvsL-GuZxtPaUfJ9uLr-ViTho9Tlz-a53y3hB_KcXqXt3gO8JNHL2-ouv5Xp9gL7ehlXWu4ywcH87Ur5vn7htCpS3Ep7l2j2N34PKD_IfQ_4rewh9ygfOaVZW0o6yl9UDMy8YyNHyfHIEhPPEL84KEwcxGMx6fxfG942ZXKWPtjJsdyzA1kh311IFEu-Bzh_IQjirgEdpl19ynGv5Bx6OZa6v_96vbu0RveM4g2tQRYtlxzjzvgDD3uKTFxfy4S1lteL5lWu9eTIuJE3b78o_7eyYXlem1zyccwkTwi_vY8q1yV4USTf8h_kt7-_ZZfv3z7yQ3B6xzeWL4L35DBkyXpx4IehRO1N2dLG6QJ8-bJHT2Ehsj09blfkKHVBQvy2k-vVpI_VsGsgjn7IHNy2TOrL2n6E-O-mXL4ZXpa_-1_Sz8AHSQR81cEd9nx-8AedjwTT_iTYpx6fUnYzOS25az_rimSv_ar7eJ32FgIP9Iebs_BActgHOZ_lKmd1LFU1SNIILI1KUBmLZt8d9hm_7dgc90W6He336lx-z8_C-OgfyO-d5BvO9T-Xz91f_2vpK_e-hJqCX3WNxOMpZ182R15HsI1QdVmts1GvlRG-UAS6g5mMiTL15p1_7mrPgQx2HKu6qvbIOJKy1dsGL3GFx82I-aYnkC_JuK1ps65wQnr69r6gfDWQt-dI5Sdx6BUNfqjp8sBpkp3x8jesMzizWuCcLGi-edgtY06aXUjfzqOSLWfc3s1aOz5UZszXvTMGf9pp-arT0YxlHBmZKNRe4yMou0h_mOnC_GrzNuge8MhF-1aRULBFa-dp_EVyk1_7Ang5uWfzg3MWd2YNapIcS4w155xJ11zms3kMsanHBWqn_JNOZ628VMtRFcmVf8MOJk9StOg9rCHwmcn4snF4Ph2xqbtEbB98v11PWEn5NNSUBBu5YKzq-5xnG2ZxUwPyC9gXlOKg8jxLGegrbI2MaHHz6kt-b0uEDT6BuTjluzlXVx72Ycf4U9UiuIcKMu8JO4ogJ0fZreDvc86IP9mrK8ud00jHND1TtPJqEeUs0DUL7C2YjPkpxuFcVs36Bh9mldznYCOg5eM9yjNBco9-xTj2MF5xvERWXin7H1lLIFczi9tSPJQG_wJORW4eAqzqzdsbkbbSc8C5VMSmhjCRdUAvKNCi_X_fiDhALO61DUWRh72HOfOqKz258bnQmCfdSowGdVH5sdRTX6K9tkNL532no49jT9M5d-7fmVSjjtep3sZPk47eJzwTZe28DjXtXa93kIttLZhymhwPJ1tfB-ULGjNAzdN7CDI1p9J8Q3yadajZF8OTyDRtUXJZf-a4iyCuvmH8MvGwz1DyJ00TcjMT5aDrWzzQpHT3NpD3R70OvjKYmgN-Uwrt02FTHA9mDeJ6BLqaPw64yLtD2wmwlBPT1MlxQEvX2XwR-niQhRqDACbtrT_FAUFGrwhqPMhp9VykmalfJAe_kzljytMIeqaL6Z-iLDL1SWQXlwbEorEnjHUehJi714XBwiAvpyqv_WcdDwUCTMLb7nTMFu_oEPw3ab_Y8uGi-IDehgrPNjCPO7__gVzn8C5x4dwbKvtBLg9opPPkogG5RWnmuLJrgMUtDjgDBrc9WbK2duXQx-q4F7giplbL2L6bM4CrwDc9X_mC2i_8mIr7Anny_YHaHng5NW0j4tcF6AW3SN9zSqobOxq-Q6h_yK8rsbS1tgv4QNiT2ddgPMeuwjDXQo1VXqhourN4N2-59cO2sr1sXBXo7NQWtU_mmuqUe5gS1kxdhDF2xswZ3wc7tv4OuL2xNRxsNt_5S62xC61V3W4oUrXd9vYIMDw6a1sLmv92xqzTd8G5c1lrnbBW5SKzr3fGeKo1em71Lmncpp4tfFJ2TLwaM9I883mD2invVDEhqMyvg8WdsaR5MfVd0KGMTM8J-XvWM2C_09HkU_m9g7g7O_OxGGy_dcq3N9NHKsxK5lyNLwX3-G1kb-G_hcj9T04ewBeb3-QatvlDrs1vJJONjP81NDGxKcfWBinR-SYiyPrbSeLjOfag_wEsiD9PqePv_H4j6Do4mK8p-RppewK-6cx5GBueRRbRFCPLM8S_8QHoCTTeOLLGw10iBixm6ifk2xmLCHxhli_IQ2vP3kV6MTSp3Gv7e7Dh3r4npQpjOPjL9ujAI3fqC_iE1v_a9tzpobHYZdGr_GZ6WqeGHMEfbO8bQP63a3kWOGu96g81jZQ-OroGmlVHkDtfePNTSmv3fCFxrjMn6Cly5xBH7t2Qh1y-tjefVmz3igPY19ZLBnpTeD5SdBqJ4z05EuzeA3h-Y-mqdxF9J_Q7wtPNp0uHJU8undZiQsiz_PBoz4Hc0sdnPABYBRm7ogoRYdeg9jhrIHNtbP4F9VLiuHG33AXKVyOQW-NqyEmloYNDdZ-4Pqm3ujRWb1BKHwLiAPompw8IyXHjxn_o8BAytK6NLgXUYNWbd9CnP3t1iXl1edva96-7yQ3qvWt-xzTYL-8t3rNvMIHCRFN_pd-_oymGHkb11rVUPcBUM5qdef9SWHvCm-dS4JvCu8DvKfFkbHUslfpNm9s6q_LWkX57K976b7281blWvsXIcbhrVgEe9wvcqPeY4UOUWt5H8qox3sXI_uHgJ8ChvaGX7O_OeLD7LR7-sL0m3E9bDzckVGF7a4-Kqz7cxSOp81aXhAF1ewiRccq9Gt47_PUOf2GZbM87nWOPcU2Xtn4SEZs1qK21c66n0n9Mj4S5-459km9jqYeNesCeGt-HMi_r2gGYt6Omnzw8_dr_88__AJP7SDM0GQAA",
        "s": "eyJzZHMiOnsiZCI6MCwiZyI6MCwiYyI6MCwiZXMiOjAsImNzIjowLCJwcyI6MCwia3MiOjAsImZzIjowLCJkcyI6MCwibCI6IiIsInVwcyI6NCwidWFzIjowLCJwbCI6NH19",
        "sk": "{{sk}}",
        "ui": "{{ui}}"
      }
        """.trimIndent()
    )

    response(
        """
        
    """.trimIndent()
    )

    extract {
        "pi" from "pi"
    }

    onSuccess { true }
}