package org.example.ms072.constants;

public class SkillType {
    
    public final static class 管理员 {

        public final static int 治愈_魔法无效 = 9001000;// 技能描述： - [最高等级 : 1]\n敌人的魔法效果无效化，治愈自己和周边角色的状态异常，恢复HP。
        public final static int 轻功 = 9001001;// 技能描述： - [最高等级 : 1]\n15分内增加周边角色的移动速度和跳跃力。
        public final static int 圣化之力 = 9001002;// 技能描述： - [最高等级 : 1]\n15分内周边角色打怪时，取得更多经验值。
        public final static int 祝福 = 9001003;// 技能描述： - [最高等级 : 1]\n15分内周边角色的各种能力值大幅增加。
        public final static int 隐藏术 = 9001004;// 技能描述： - [最高等级 : 1]\n隐藏自己，其他角色看不见。使用技能会被解除。
        public final static int 复活 = 9001005;// 技能描述： - [最高等级 : 1]\n使得死亡的角色复活。
        public final static int 超级狂龙斩 = 9001006;// 技能描述： - [最高等级 : 1]\n巨龙的咆哮，能打败15只以下的敌人。
        public final static int 缩地大法 = 9001007;// 技能描述： - [最高等级 : 1]\n用‘上下左右’方向键可以瞬间移动一定的距离.
        public final static int 神圣之火 = 9001008;// 技能描述： - [最高等级:1]15分钟内,强化肉体全体最大HP和最大MP提升60%
        public final static int 测试外挂 = 9001009;// 技能描述： - 最高等级：1]\n 使用测谎仪测试可以玩家
    }
    
    public final static class 新手 {

        public final static int 群宠 = 8;// 技能描述： - [最高等级：1]可携带多只宠物，最多携带3只。
        public final static int 蜗牛投掷术 = 1000;// 技能描述： - [最高等级：3]\n投掷蜗牛壳来攻击远距离的怪物。
        public final static int 团队治疗 = 1001;// 技能描述： - [最高等级 : 3]\n30秒内持续恢复HP. \n#c再次使用时间间隔 : 2分#
        public final static int 疾风步 = 1002;// 技能描述： - [最高等级 : 3]\n瞬间迅速的移动。 \n#c再次使用时间间隔 ： 1分#
        public final static int 匠人之魂 = 1003;// 技能描述： - [最高等级 : 1]\n不能装备的道具也能使用符咒
        public final static int 骑兽技能 = 1004;// 技能描述： - [最高等级 : 1]\n能够坐骑怪物并移动
        public final static int 英雄之回声 = 1005;// 技能描述： - [最高等级 : 1]\n增加周边角色的物理攻击力和魔法攻击力。 \n#c冷却时间 : 2小时#
        public final static int 向下跳跃 = 1006;// 技能描述： - [最终等级 : 1]\n向下方向跳跃。
        public final static int 锻造 = 1007;// 技能描述： - [最高等级 : 3]\n可以使用炼金术制作物品。根据自身角色等级的不同，可制作的物品不同。
        public final static int 流星竹雨 = 1009;// 技能描述： - 使用千万个竹子攻击敌人
        public final static int 金刚霸体 = 1010;// 技能描述： - 一定时间内变成无敌状态
        public final static int 狂暴战魂 = 1011;// 技能描述： - 一定时间内增加伤害值
    }

    public final static class 战士 {

        public final static int 生命恢复 = 1000000;// 技能描述： - [最高等级 : 16]\n增加每5秒的HP恢复量.
        public final static int 生命加强 = 1000001;// 技能描述： - [最高等级 : 10]\n等级上升时以及使用AP增加HP，提高最大HP的增加量。\n必需技能:#c提高恢复HP等级5以上#
        public final static int 恢复术 = 1000002;// 技能描述： - [最高等级 : 8]\n在梯子和绳索上面也可以恢复HP.\n必需技能: #c提高恢复HP等级3以上#
        public final static int 圣甲术 = 1001003;// 技能描述： - [最高等级 : 20]\n一定时间内物理防御力增加.\n必需技能：#c恢复术等级3以上#
        public final static int 强力攻击 = 1001004;// 技能描述： - [最高等级 : 20]\n消费MP，用所装备的武器给怪物以致命一击.
        public final static int 群体攻击 = 1001005;// 技能描述： - [最高等级 : 20]\n消费HP和MP，用装备的武器同时攻击周围的多个怪物.\n必需技能：#c强力攻击等级1以上#
    }

    public final static class 剑客 {

        public final static int 精准剑 = 1100000;// 技能描述： - [最高等级 : 20]\n增加使用剑系武器的命中率及熟练度(必须装备单手剑或双手剑)
        public final static int 精准斧 = 1100001;// 技能描述： - [最高等级 : 20]\n增加使用斧系武器的命中率及熟练度.(必须装备单手斧，双手斧)
        public final static int 终极剑 = 1100002;// 技能描述： - [最高等级 : 30]\n第一次攻击后发动连续攻击。(必须装备单手剑或双手剑)\n必需技能 : #c精准剑等级3以上#
        public final static int 终极斧 = 1100003;// 技能描述： - [最高等级 : 30]\n第一次攻击后发动连续攻击.(必须装备单手斧，双手斧)\n必需技能 : #c精准斧等级3以上#
        public final static int 快速剑 = 1101004;// 技能描述： - [最高等级 : 20]\n消费HP和MP,攻击速度提升一个等级.(必须装备单手剑，双手剑)\n必需技能 : #c精准剑等级5以上#
        public final static int 快速斧 = 1101005;// 技能描述： - [最高等级 : 20]\n攻击速度提升一个等级.(装备单手斧，双手斧）\n必需技能：#c精准斧等级5以上#
        public final static int 愤怒之火 = 1101006;// 技能描述： - [最高等级 : 20]\n提升周围组队成员的物理攻击力，降低物理防御力.
        public final static int 伤害反击 = 1101007;// 技能描述： - [最高等级 : 30]\n按怪物对自身伤害的一定量反击怪物。(怪物所受的伤害以最大HP10%为限度)\n必需技能:#c愤怒之火等级3以上#
    }

    public final static class 勇士 {

        public final static int 魔力恢复 = 1110000;// 技能描述： - [最高等级:20]\n增加每10秒的MP恢复量。
        public final static int 盾防精通 = 1110001;// 技能描述： - [最高等级:20]盾牌的物理防御力增加，必需装备盾牌.
        public final static int 斗气集中 = 1111002;// 技能描述： - [最高等级:30]\n进入斗气集中状态。每次攻击会累积1个斗气，最高累积5个斗气。伤害以3个斗气为基准.
        public final static int 狂乱之剑 = 1111003;// 技能描述： - [最高等级：30]\n对一个敌人进行攻击。必须装备剑且处于斗气集中状态。\n必需技能:#c斗气集中等级1以上#
        public final static int 狂乱之斧 = 1111004;// 技能描述： - [最高等级：30]\n给一个敌人进行攻击。必须装备斧且处于斗气集中状态。\n必需技能:#c斗气集中等级1以上#
        public final static int 气绝剑 = 1111005;// 技能描述： - [最高等级：30]\n攻击怪物，一定几率使怪物昏迷.必须装备剑且处于斗气集中状态。\n必需技能:#c斗气集中等级1以上#
        public final static int 气绝斧 = 1111006;// 技能描述： - [最高等级：30]\n攻击怪物，一定几率使怪物昏迷.必须装备斧且处于斗气集中状态。\n必要技能：#c斗气集中等级1以上#
        public final static int 防御崩坏 = 1111007;// 技能描述： - [最高等级：20]\n一定的几率使多个怪物的物理防御无效，对物理防御状态的怪物有效。\n必需技能:#c虎咆哮等级3以上#
        public final static int 虎咆哮 = 1111008;// 技能描述： - [最高等级：30]\n向周边怪物发出虎吼施加伤害，同时一定几率让其昏迷。一次不能攻击6只以上。
    }

    public final static class 英雄 {

        public final static int 进阶斗气 = 1120003;// 技能描述： - [最高等级：30]\n最高斗气量增加为10，而以一定几率斗气量以两个为单位累积，需完成斗气集中才可提升技能。\n必要技能：#c斗气集中等级30以上#
        public final static int 阿基里斯 = 1120004;// 技能描述： - 永久强化盔甲,减弱伤害值.
        public final static int 寒冰掌 = 1120005;// 技能描述： - 一定概率下用盾牌挡住敌人的攻击.近距离攻击防御成功的话,攻击自己的敌人2秒内处于昏迷状态.只有装备盾牌的时候技能才有效.
        public final static int 冒险岛勇士 = 1121000;// 技能描述： - 一定时间内把组队成员的所有属性点提高一定的百分比.
        public final static int 磁石 = 1121001;// 技能描述： - 把远处的敌人最多能6只吸到自己面前.
        public final static int 稳如泰山 = 1121002;// 技能描述： - 凭借着强韧的精神，受到敌人的攻击仍不会后退。
        public final static int 突进 = 1121006;// 技能描述： - 向前攻击,能把自己前面的10个敌人推开.
        public final static int 轻舞飞扬 = 1121008;// 技能描述： - 把眼前的好几个敌人,连续攻击2次.
        public final static int 英雄之斧 = 1121009;// 技能描述： - 用闪光的斧子砍敌人3次.最多可攻击3只怪.
        public final static int 葵花宝典 = 1121010;// 技能描述： - 使用10个斗气能量,在一定时间内提高攻击力\n#c冷却时间 : 8分#
        public final static int 勇士的意志 = 1121011;// 技能描述： - 从异常状态中恢复. 随着级上升,能恢复异常的能力也上升.. \n#c冷却时间 : 10分#
    }

    public final static class 准骑士 {

        public final static int 精准剑 = 1200000;// 技能描述： - [最高等级 : 20]\n增加使用剑系武器的命中率及熟练度.(必须装备单手剑，双手剑)
        public final static int 精准钝器 = 1200001;// 技能描述： - [最高等级 : 20]\n增加使用钝器的命中率及熟练度。(必须装备单手钝器或双手钝器)
        public final static int 终极剑 = 1200002;// 技能描述： - [最高等级 : 30]\n第一次攻击后发动连续攻击.(必须装备单手剑，双手剑)\n必需技能: #c精准剑等级3以上#
        public final static int 终极钝器 = 1200003;// 技能描述： - [最高等级 : 30]\n第一次攻击后发动连续攻击。(必须装备单手钝器或双手钝器)\n必需技能: #c精准钝器等级3以上#
        public final static int 快速剑 = 1201004;// 技能描述： - [最高等级 : 20]\n攻击速度提升一个等级.(装备单手剑，双手剑)\n必需技能: #c精准剑等级5以上#
        public final static int 快速钝器 = 1201005;// 技能描述： - [最高等级 : 20]\n功击速度提升一个等级。(必须装备单手钝器或双手钝器)\n必需技能: #c精准钝器等级5#
        public final static int 压制术 = 1201006;// 技能描述： - [最高等级 : 20]\n消费MP, 让周围的怪物感到压制感，降低怪物的物理攻击力与物力防御力。
        public final static int 伤害反击 = 1201007;// 技能描述： - [最高等级 : 30]\n按怪物对自身伤害的一定量反击怪物。(怪物所受的伤害以最大HP10%为限度)\n必需技能：#c压制术等级3以上#
    }

    public final static class 骑士 {

        public final static int 魔力恢复 = 1210000;// 技能描述： - [最高等级:20]\n增加每10秒的MP恢复量
        public final static int 盾防精通 = 1210001;// 技能描述： - [最高等级:20]\n盾牌的物理防御力增加,必需装备盾牌.
        public final static int 属性攻击 = 1211002;// 技能描述： - [最高等级:30]\n赋予武器全属性, 攻击6个以下多个怪物。一定的几率使怪物昏迷。
        public final static int 烈焰之剑 = 1211003;// 技能描述： - [最高等级:30]\n一定时间内,给剑赋予火焰属性。超过时间或使用属性攻击会被取消。
        public final static int 烈焰钝器 = 1211004;// 技能描述： - [最高等级:30]\n一定时间内,给钝器赋予火焰属性。超过时间或使用属性攻击会被取消.
        public final static int 寒冰之剑 = 1211005;// 技能描述： - [最高等级:30]\n一定时间内,给剑赋予冰属性.超过时间或使用属性攻击会被取消.
        public final static int 寒冰钝器 = 1211006;// 技能描述： - [最高等级:30]\n一定时间内,给钝器赋予冰属性。超过时间或使用属性攻击会被取消.
        public final static int 雷电之击_剑 = 1211007;// 技能描述： - [最高等级:30]\n一定时间内,给剑赋予雷属性。超过时间或使用属性攻击会被取消.
        public final static int 雷电之击_钝器 = 1211008;// 技能描述： - [最高等级:30]\n一定时间内,给钝器赋予雷属性。超过时间或使用属性攻击会被取消.
        public final static int 魔击无效 = 1211009;// 技能描述： - [最高等级:20]\n一定的几率使多个怪物的魔法防御无效。对使用魔法防御状态的怪物有效。\n必需技能: #c属性攻击等级3以上#
    }

    public final static class 圣骑士 {

        public final static int 阿基里斯 = 1220005;// 技能描述： - 永久强化盔甲,减弱伤害值.
        public final static int 寒冰掌 = 1220006;// 技能描述： - 一定概率下用盾牌挡住敌人的攻击.近距离攻击防御的话,攻击自己的敌人2秒内处于昏迷状态.只有装备盾牌的时候技能才有效.
        public final static int 万佛归一破 = 1220010;// 技能描述： - 使用属性攻击的时候伤害上升, 一定几率造成昏迷状态.\n必要技能 : #c属性攻击 30级#
        public final static int 冒险岛勇士 = 1221000;// 技能描述： - 一定时间内把组队成员的所有属性点提高一定的百分比.
        public final static int 磁石 = 1221001;// 技能描述： - 把远处的敌人最多能6只吸到自己面前.
        public final static int 稳如泰山 = 1221002;// 技能描述： - 凭借着强韧的精神，受到敌人的攻击仍不会后退。
        public final static int 圣灵之剑 = 1221003;// 技能描述： - 在一定时间内赋予剑圣属性。若已到时间或使用属性攻击便会取消。
        public final static int 圣灵之锤 = 1221004;// 技能描述： - 在一定时间内赋予棍圣属性。若以到时间或使用属性攻击便会取消。
        public final static int 突进 = 1221007;// 技能描述： - 向前攻击,能把自己前面的敌人推开.
        public final static int 连环环破 = 1221009;// 技能描述： - 给一个敌人一次强攻击
        public final static int 圣域 = 1221011;// 技能描述： - 用大锤子敲地,攻击15只以下的敌人.受到攻击的敌人体力只剩1.
        public final static int 勇士的意志 = 1221012;// 技能描述： - 从异常状态中恢复. 随着级上升,能恢复异常的能力也上升.. \n#c冷却时间 : 10分#
    }

    public final static class 枪战士 {

        public final static int 精准枪 = 1300000;// 技能描述： - [最高等级 : 20]\n增加使用枪系武器的命中率及熟练度.(必须装备枪)
        public final static int 精准矛 = 1300001;// 技能描述： - [最高等级 : 20]\n增加使用矛系武器的命中率及熟练度.(必须装备矛)
        public final static int 终极枪 = 1300002;// 技能描述： - [最高等级 : 30]\n第一次攻击后发动连续攻击。(必须装备枪)\n必需技能: #c精准枪等级3以上#
        public final static int 终极矛 = 1300003;// 技能描述： - [最高等级 : 30]\n第一次攻击后发动连续攻击。(必须装备矛)\n必需技能: #c精准矛等级3以上#
        public final static int 快速枪 = 1301004;// 技能描述： - [最高等级 : 20]\n攻击速度提升一个等级.(必须装备枪)\n必需技能: #c精准枪等级5以上#
        public final static int 快速矛 = 1301005;// 技能描述： - [最高等级 : 20]\n攻击速度提升一个等级.(必须装备矛)\n必需技能: #c精准矛等级5以上#
        public final static int 极限防御 = 1301006;// 技能描述： - [最高等级 : 20]\n提高周围所有队员的物理防御力和魔法防御力.
        public final static int 神圣之火 = 1301007;// 技能描述： - [最高等级 : 30]\n增加周围所有队员的最大HP和最大MP.\n必需: #c极限防御等级3以上#
    }

    public final static class 龙骑士 {

        public final static int 魔法抗性 = 1310000;// 技能描述： - [最高等级:20]\n提高对所有属性魔法的抗性
        public final static int 枪连击 = 1311001;// 技能描述： - [最高等级:30]\n用枪对前方怪物连续刺击.
        public final static int 矛连击 = 1311002;// 技能描述： - [最高等级:30]\n用矛对前方怪物连续刺击.
        public final static int 无双枪 = 1311003;// 技能描述： - [最高等级:30]\n对中距离多个怪物进行攻击，最多攻击6个怪物。
        public final static int 无双矛 = 1311004;// 技能描述： - [最高等级:30]\n对中距离多个怪物进行攻击，最多攻击6个怪物。
        public final static int 龙之献祭 = 1311005;// 技能描述： - [最高等级:30]\n牺牲HP，对单体怪物进行无视物理防御的攻击。对BOSS无效，HP不会减少到1以下
        public final static int 龙咆哮 = 1311006;// 技能描述： - [最高等级:30]\n用巨大的龙的咆哮攻击15个以下的怪物。发动时HP会大幅减少, 只有HP50％以上时才可以使用.\n必需技能: #c龙之献祭等级3以上#
        public final static int 力量崩坏 = 1311007;// 技能描述： - [最高等级:20]\n一定几率使多个怪物的提高力量的技能无效。\n必需技能: #c龙之魂等级3以上#
        public final static int 龙之魂 = 1311008;// 技能描述： - [最高等级:20]\n一定的时间攻击力上升,但HP缓慢减少。HP不足时技能解除。
    }

    public final static class 黑骑士 {

        public final static int 阿基里斯 = 1320005;// 技能描述： - 永久强化盔甲,减弱伤害值.
        public final static int 恶龙附身 = 1320006;// 技能描述： - HP下降到一定水平的话,黑骑士内心中沉睡的灵魂爆发,使攻击伤害增加. 体力恢复的话,技能效果消失.
        public final static int 灵魂治愈 = 1320008;// 技能描述： - 灵魂治愈在每一定时间内补充黑骑士的体力.技能级别上升,HP恢复量也上升.\n必要技能 : #c灵魂助力 1级 以上#
        public final static int 灵魂祝福 = 1320009;// 技能描述： - 灵魂祝福在每一定时间使用状态. 根据技能等级不同发动物理防御力, 魔法防御力, 回避率, 命中率, 物理攻击力上升的状态.\n必要技能 : #c灵魂助力 1级 以上#
        public final static int 冒险岛勇士 = 1321000;// 技能描述： - 一定时间内把组队成员的所有属性点提高一定的百分比.
        public final static int 磁石 = 1321001;// 技能描述： - 把远处的敌人吸到自己面前.
        public final static int 稳如泰山 = 1321002;// 技能描述： - 凭借着强韧的精神，受到敌人的攻击仍不会后退。
        public final static int 突进 = 1321003;// 技能描述： - 向前方猛烈刺击，打倒所有阻挡在面前的敌人。
        public final static int 灵魂助力 = 1321007;// 技能描述： - 召唤灵魂助力. 用灵魂助力的力永久地提高武器熟练度.
        public final static int 勇士的意志 = 1321010;// 技能描述： - 从异常状态中恢复. 随着级上升,能恢复异常的能力也上升.. \n#c冷却时间 : 10分#
    }

    public final static class 魔法师 {

        public final static int 魔力恢复 = 2000000;// 技能描述： - [最高等级 : 16]\n增加每10秒的MP恢复量.
        public final static int 魔力强化 = 2000001;// 技能描述： - [最高等级 : 10]\n等级上升时及使用AP加MP时，提高MaxMP的增加量。\n必要技能: #c增加恢复MP等级5以上#
        public final static int 魔法盾 = 2001002;// 技能描述： - [最高等级 : 20]\n一定时间内，损血量以MP代替。但MP达到0消耗HP。
        public final static int 魔法铠甲 = 2001003;// 技能描述： - [最高等级 : 20]\n一定时间内增加物理防御力。\n必需技能: #c魔法盾等级3以上#
        public final static int 魔法弹 = 2001004;// 技能描述： - [最高等级 : 20]\n消耗MP,攻击一个怪物.
        public final static int 魔法双击 = 2001005;// 技能描述： - [最高等级 : 20]\n消耗MP,对同一个怪物连续攻击两次.
    }

    public final static class 火毒法师 {

        public final static int 魔力吸收 = 2100000;// 技能描述： - [最高等级 : 20]\n使用魔法攻击时，吸收怪物的MP。怪物的MP为0时无效.
        public final static int 精神力 = 2101001;// 技能描述： - [最高等级 : 20]\n通过精神交流提高周围所有队员的魔力。\n必需技能: #c魔力吸收等级3以上#
        public final static int 快速移动 = 2101002;// 技能描述： - [最高等级 : 20]\n用‘上下左右’方向键可以瞬间移动一定的距离.
        public final static int 缓速术 = 2101003;// 技能描述： - [最高等级 : 20]\n降低怪物的移动速度.无法重复使用,最多一次攻击6个.\n必需技能: #c快速移动等级５以上# 
        public final static int 火焰箭 = 2101004;// 技能描述： - [最高等级 : 30]\n放出火箭，攻击怪物。攻击冰属性的怪物时更有效.
        public final static int 毒雾术 = 2101005;// 技能描述： - [最高等级 : 30]\n放出毒雾，使怪物受伤和中毒.
    }

    public final static class 火毒巫师 {

        public final static int 火毒抗性 = 2110000;// 技能描述： - [最高等级:20]\n提高对火毒属性魔法的抗性
        public final static int 魔力激化 = 2110001;// 技能描述： - [最高等级:30]\n消耗更多MP,提高自己的魔法攻击力
        public final static int 末日烈焰 = 2111002;// 技能描述： - [最高等级:30]\n在自己周边引发大爆炸.范围内的怪物都遭受火属性攻击,最多攻击6个怪物。
        public final static int 致命毒雾 = 2111003;// 技能描述： - [最高等级:30]\n在周围制造毒雾。在雾中的怪物会中毒减少HP, 对中毒怪物数量没有限制。
        public final static int 封印术 = 2111004;// 技能描述： - [最高等级:20]\n对周围多个怪物进行封印。成为封印状态的怪物不能使用技能, 对BOSS无效\n必需技能: #c魔力激化等级3以上#
        public final static int 魔法狂暴 = 2111005;// 技能描述： - [最高等级:20]\n消耗更多的HP和MP, 提高魔法攻击速度.\n必需技能: #c魔力激化等级3以上#
        public final static int 火毒合击 = 2111006;// 技能描述： - [最高等级:30]\n混合火和毒属性的魔法攻击一个怪物,一定的几率使怪物中毒。
    }

    public final static class 火毒魔导师 {

        public final static int 冒险岛勇士 = 2121000;// 技能描述： - 一定时间内把组队成员的所有属性点提高一定的百分比.
        public final static int 创世之破 = 2121001;// 技能描述： - 一定时间内集中周围的微小颗粒的能量，一次性引爆强烈的爆炸
        public final static int 魔法反击 = 2121002;// 技能描述： - 改变魔法的流向来攻击敌人.一次最多返还敌人的20%体力
        public final static int 火凤球 = 2121003;// 技能描述： - 火凤球用火柱环绕怪物,给予持续伤害。被火凤球抓住的人冰系抗性降低。
        public final static int 终极无限 = 2121004;// 技能描述： - 一定时间内搜集周围的魔力,不消耗魔法值. \n#c冷却时间 : 10分#
        public final static int 冰破魔兽 = 2121005;// 技能描述： - 一定时间内召唤冰属性的冰破魔兽. 最多攻击3只敌人.\n必要技能 : #c火凤球等级5以上#
        public final static int 连环爆破 = 2121006;// 技能描述： - 给一个敌人伤害后,一定时间内使之麻痹.
        public final static int 天降落星 = 2121007;// 技能描述： - 从天空召唤陨石,向15只以下的多数怪物给予强烈的火焰伤害.
        public final static int 勇士的意志 = 2121008;// 技能描述： - 从异常状态中恢复. 随着级上升,能恢复异常的能力也上升.. \n#c冷却时间 : 10分#
    }

    public final static class 冰雷法师 {

        public final static int 魔力吸收 = 2200000;// 技能描述： - [最高等级 : 20]\n使用魔法攻击时，吸收怪物的MP。怪物的MP为0时无效.
        public final static int 精神力 = 2201001;// 技能描述： - [最高等级 : 20]\n通过精神交流暂时提高周围所有队员的魔力.\n必要技能 : #c魔力吸收等级3以上#
        public final static int 快速移动 = 2201002;// 技能描述： - [最高等级 : 20]\n用‘上下左右’方向键可以瞬间移动一定的距离。
        public final static int 缓速术 = 2201003;// 技能描述： - [最高等级 : 20]\n降低怪物的移动速度.无法重复使用,最多一次攻击6个.\n必需技能: #c快速移动等级5以上#
        public final static int 冰冻术 = 2201004;// 技能描述： - [最高等级 : 30]\n使用冰冻魔法，使怪物冻结并受到伤害.\n被打中的敌人暂时停止行动.火属性的敌人受到伤害更大.
        public final static int 雷电术 = 2201005;// 技能描述： - [最高等级 : 30]\n在周围制造强力磁场，并向怪物发起雷电攻击，攻击一定范围内的所有敌人
    }

    public final static class 冰雷巫师 {

        public final static int 冰雷抗性 = 2210000;// 技能描述： - [最高等级:20]\n提高对冰雷属性的魔法抗性.
        public final static int 魔力激化 = 2210001;// 技能描述： - [最高等级:30]\n消耗更多的MP,提高自己的魔法攻击力
        public final static int 冰咆哮 = 2211002;// 技能描述： - [最高等级:30]\n使用冰块攻击。命中时有非冰属性的怪物冻结, 最多伤害6个怪物。
        public final static int 落雷枪 = 2211003;// 技能描述： - [最高等级:30]\n集合雷气制造枪攻击一个怪物.怪物遭受雷属性攻击.
        public final static int 封印术 = 2211004;// 技能描述： - [最高等级:20]\n对周围多个的怪物进行封印。成为封印状态的怪物不能使用技能, 对BOSS无效\n必需技能: #c魔力激化等级3以上#
        public final static int 魔法狂暴 = 2211005;// 技能描述： - [最高等级:20]\n消耗更多的HP和MP,提高魔法攻击速度。\n必需技能: #c魔力激化等级3以上#
        public final static int 冰雷合击 = 2211006;// 技能描述： - [最高等级:30]\n混合冰和雷属性的魔法攻击一个怪物。一定的几率怪物被冻结。
    }

    public final static class 冰雷魔导师 {

        public final static int 冒险岛勇士 = 2221000;// 技能描述： - 一定时间内把组队成员的所有属性点提高一定的百分比.
        public final static int 创世之破 = 2221001;// 技能描述： - 一定时间内搜集周围离子的力量,引起一次性大爆炸
        public final static int 魔法反击 = 2221002;// 技能描述： - 改变魔法的流向来攻击敌人.一次最多返还敌人的20%体力
        public final static int 冰凤球 = 2221003;// 技能描述： - 冻结多数怪物,给予持续伤害.被冰凤球抓住的人火系抗性降低.
        public final static int 终极无限 = 2221004;// 技能描述： - 一定时间内搜集周围的魔力,不消耗魔法值 \n#c冷却时间 : 10分#
        public final static int 火魔兽 = 2221005;// 技能描述： - 一定时间内召唤火属性的火魔兽. 最多攻击3只敌人.\n必要技能 : #c冰凤球等级5以上#
        public final static int 链环闪电 = 2221006;// 技能描述： - 使用高压电,给敌人电属性攻击.被电击的敌人周围要是有其他敌人,同样会受伤害.
        public final static int 落霜冰破 = 2221007;// 技能描述： - 从天空掉冰枪,给15只以下的怪物强烈的冰属性伤害.
        public final static int 勇士的意志 = 2221008;// 技能描述： - 从异常状态中恢复. 随着级上升,能恢复异常的能力也上升.. \n#c冷却时间 : 10分#
    }

    public final static class 牧师 {

        public final static int 魔力吸收 = 2300000;// 技能描述： - [最高等级 : 20]\n使用魔法攻击时，吸收怪物的MP。怪物的MP为0时无效.
        public final static int 快速移动 = 2301001;// 技能描述： - [最高等级 : 20]\n用‘上下左右’方向键可以瞬间移动一定的距离.
        public final static int 群体治愈 = 2301002;// 技能描述： - [最高等级 : 30]\n恢复周围成员的HP。根据周围成员人数恢复的HP值将不同（对周围黑暗属性的怪物造成伤害）
        public final static int 神之保护 = 2301003;// 技能描述： - [最高等级 : 20]\n减轻受到物理攻击的伤害.（对魔法攻击无效）\n必要技能 : #c群体治愈等级5以上#
        public final static int 祝福 = 2301004;// 技能描述： - [最高等级 : 20]\n一定时间内，周围组队员的命中率、回避率、物理防御、魔法防御的能力值上升。弓箭手的技能：集中术和各种药水不能一起使用.\n必要技能 : #c神之保护等级5以上#
        public final static int 圣箭术 = 2301005;// 技能描述： - [最高等级 : 30]\n变出圣箭，攻击一个敌人。
    }

    public final static class 祭司 {

        public final static int 魔法抗性 = 2310000;// 技能描述： - [最高等级:20]\n提高对所有属性魔法攻击的抗性。
        public final static int 净化 = 2311001;// 技能描述： - [最高等级:20]\n一定范围内怪物的魔法无效，同时治疗包括自己及周边组员的状态异常。
        public final static int 时空门 = 2311002;// 技能描述： - [最高等级:20]\n制造通向最近村落的时空门，使组队成员可以通过时空门回到最近的村落。在传送点按↑可以移动\n必需技能 : #c净化等级3以上#
        public final static int 神圣祈祷 = 2311003;// 技能描述： - [最高等级:30]\n组队时候,能得到更多经验值。只有两个人以上的组队状态下才能发挥100%效果。\n必需技能 : #c净化等级3以上#
        public final static int 圣光 = 2311004;// 技能描述： - [最高等级:30]\n利用神圣光芒攻击多个怪物。对亡灵族怪物和恶魔怪物造成更多伤害。
        public final static int 巫毒术 = 2311005;// 技能描述： - [最高等级:30]\n使周围怪物变成蜗牛,攻击力和移动速度均下降。对BOSS无效,最多变化6个怪物。
        public final static int 圣龙召唤 = 2311006;// 技能描述： - [最高等级:30]\n召唤圣龙守护主人，并攻击怪物。技能点增加，可以召唤更强的龙。
    }

    public final static class 主教 {

        public final static int 冒险岛勇士 = 2321000;// 技能描述： - 一定时间内把组队成员的所有属性点提高一定的百分比.
        public final static int 创世之破 = 2321001;// 技能描述： - 一定时间内搜集周围离子的力量,引起一次性大爆炸
        public final static int 魔法反击 = 2321002;// 技能描述： - 用内力,改变魔法的流向来攻击敌人.一次最多返还敌人的20%体力
        public final static int 强化圣龙 = 2321003;// 技能描述： - 一定时间内召唤圣龙.最多攻击3个敌人\n必需技能 : #c圣龙召唤等级15以上#
        public final static int 终极无限 = 2321004;// 技能描述： - 一定时间内搜集周围的魔力,不消耗魔法值 \n#c冷却时间 : 10分#
        public final static int 圣灵之盾 = 2321005;// 技能描述： - 一定时间内防止组队成员中异常状态. \n#c冷却时间 : 2分#
        public final static int 复活术 = 2321006;// 技能描述： - 用神圣的光召唤组队成员的灵魂,使之复活.
        public final static int 光芒飞箭 = 2321007;// 技能描述： - 用神圣的弓箭,给一个敌人圣属性攻击.
        public final static int 圣光普照 = 2321008;// 技能描述： - 从天空掉落神圣的光,给15只以下的敌人给予圣属性伤害
        public final static int 勇士的意志 = 2321009;// 技能描述： - 从异常状态中恢复. 随着级上升,能恢复异常的能力也上升.. \n#c冷却时间 : 10分#
    }

    public final static class 弓箭手 {

        public final static int 精准箭 = 3000000;// 技能描述： - [最高等级 : 16]\n增加攻击命中率.
        public final static int 强力箭 = 3000001;// 技能描述： - [最高等级 : 20]\n可以增加箭的攻击力.
        public final static int 远程箭 = 3000002;// 技能描述： - [最高等级 : 8]\n增加射程距离.\n必需技能 : #c精准箭等级3以上#
        public final static int 集中术 = 3001003;// 技能描述： - [最高等级 : 20]\n增加命中率和回避率.\n必需技能 : #c精准箭等级3以上#
        public final static int 断魂箭 = 3001004;// 技能描述： - [最高等级 : 20]\n强力的箭矢，造成强大的杀伤力。
        public final static int 二连射 = 3001005;// 技能描述： - [最高等级 : 20]\n一次射出两支箭，攻击怪物两次。\n必需技能 : #c断魂箭等级1以上#
    }

    public final static class 猎人 {

        public final static int 精准弓 = 3100000;// 技能描述： - [最高等级 : 20]\n增加使用弓系武器的命中率及熟练度。(必须装备弓)
        public final static int 终极弓 = 3100001;// 技能描述： - [最高等级 : 30]\n使用攻击技能后一定几率发动连续攻击. （必须装备弓）.\n必要技能 : #c精准弓３级以上#
        public final static int 快速箭 = 3101002;// 技能描述： - [最高等级 : 20]\n攻击速度提升一个等级.(必须装备弓)\n必需技能 : #c精准弓等级5以上#
        public final static int 强弓 = 3101003;// 技能描述： - [最高等级 : 20]\n用弓攻击时使怪物后退的比率增加，随着等级上升击退怪物数量会增加怪物后退的概率增加，随着等级上升击退怪物数量会增加.
        public final static int 无形箭 = 3101004;// 技能描述： - [最高等级 : 20]\n一定时间内，攻击时不消耗箭。\n必需技能 : #c快速箭等级5以上#
        public final static int 爆炸箭 = 3101005;// 技能描述： - [最高等级 : 30]\n用装备爆裂弹的箭攻击怪物，能造成周围的怪物(最多6名)受到伤害及晕倒
    }

    public final static class 射手 {

        public final static int 疾风步 = 3110000;// 技能描述： - [最高等级:20]\n增加移动速度。
        public final static int 贯穿箭 = 3110001;// 技能描述： - [最高等级:20]\n即使怪物靠得很近也能射箭，一定几率必杀怪物。
        public final static int 替身术 = 3111002;// 技能描述： - [最高等级:20]\n一定时间制造自己的分身。分身存在的时候, 怪物攻击分身。
        public final static int 烈火箭 = 3111003;// 技能描述： - [最高等级:30]\n用火属性的箭攻击多个怪物。最多攻击6个怪物，只有装备弓箭才能使用。
        public final static int 箭雨 = 3111004;// 技能描述： - [最高等级:30]\n向天空射多支箭以攻击多个怪物。最多攻击6个,只有装备弓箭才能使用。\n必需技能 : #c贯穿箭等级5以上#
        public final static int 银鹰召唤 = 3111005;// 技能描述： - [最高等级:30]\n召唤银色老鹰.在一定的时间内攻击附近的怪物。\n必需技能 : #c替身术等级5以上#
        public final static int 箭扫射 = 3111006;// 技能描述： - [最高等级:30]\n向一个怪物连续射4支箭。
    }

    public final static class 神射手 {

        public final static int 神箭手 = 3120005;// 技能描述： - 提高弓系武器的熟练度和攻击力. 只可适用于拿弓系武器的时候.\n必要技能 : #c精准弓 20级#
        public final static int 冒险岛勇士 = 3121000;// 技能描述： - 一定时间内把组队成员的所有属性点提高一定的百分比.
        public final static int 火眼晶晶 = 3121002;// 技能描述： - 赋予组队成员针对敌人寻找弱点并给予敌人致命伤的能力.
        public final static int 飞龙冲击波 = 3121003;// 技能描述： - 召唤龙的灵魂发射强有力的箭. 被箭射中的敌人被退后很远
        public final static int 暴风箭雨 = 3121004;// 技能描述： - 像暴风雨一样快速的发射箭. 摁住此技能键的状态可持续发射
        public final static int 火凤凰 = 3121006;// 技能描述： - 一定时间内召唤火属性的火凤凰. 最多攻击4只怪物.\n必要技能 : #c银鹰召唤 15级 以上#
        public final static int 击退箭 = 3121007;// 技能描述： - 用一定几率攻击敌人的腿部,使敌人降低移动速度.
        public final static int 集中精力 = 3121008;// 技能描述： - 一定时间内攻击力上升,技能使用时的魔法值也下降.\n#c 冷却时间 : 6分#
        public final static int 勇士的意志 = 3121009;// 技能描述： - 从异常状态中恢复. 随着级上升,能恢复异常的能力也上升.. \n#c冷却时间 : 10分#
    }

    public final static class 弩弓手 {

        public final static int 精准弩 = 3200000;// 技能描述： - [最高等级 : 20]\n增加使用弩系武器的命中率及熟练度。(必须装备弩)
        public final static int 终极弩 = 3200001;// 技能描述： - [最高等级 : 30]\n第一次攻击后发动连续攻击。(必须装备弩)\n必需技能 : #c精准弩等级3以上#
        public final static int 快速弩 = 3201002;// 技能描述： - [最高等级 : 20]\n攻击速度提升一个等级.(必须装备弩)\n必需技能 : #c精准弩等级5以上#
        public final static int 强弩 = 3201003;// 技能描述： - [最高等级 : 20]\n用弩攻击时，使怪物后退的比率增加，随着等级上升击退怪物数量会增加
        public final static int 无形箭 = 3201004;// 技能描述： - [最高等级 : 20]\n一定时间内，攻击时不消耗箭\n必需技能 : #c快速弩等级5以上#
        public final static int 穿透箭 = 3201005;// 技能描述： - [最高等级 : 30]\n使用钢铁弩箭贯穿怪物，最多能同时攻击6个怪物，但怪物伤害依数量而減少
    }

    public final static class 游侠 {

        public final static int 疾风步 = 3210000;// 技能描述： - [最高等级:20]\n增加移动速度。
        public final static int 贯穿箭 = 3210001;// 技能描述： - [最高等级:20]\n即使怪物靠得很近的时候也能发射弩，一定几率必杀怪物。
        public final static int 替身术 = 3211002;// 技能描述： - [最高等级:20]\n一定时间制造自己的分身。分身存在的时候, 怪物攻击分身。
        public final static int 寒冰箭 = 3211003;// 技能描述： - [最高等级:30]\n用冰属性的箭攻击多个怪物。最多攻击6个怪物，只有装备弩弓才能使用.
        public final static int 升龙弩 = 3211004;// 技能描述： - [最高等级:30]\n向地下射箭然后钻上来攻击怪物。最多攻击6个怪物,只有装备弩弓才能使用.\n必需技能 : #c贯穿箭等级5以上#
        public final static int 金鹰召唤 = 3211005;// 技能描述： - [最高等级:30]\n召唤金色老鹰。在一定的时间内攻击附近的怪物。\n必需技能 : #c替身术等级5以上#
        public final static int 箭扫射 = 3211006;// 技能描述： - [最高等级:30]\n向单个怪物连续射4支箭。
    }

    public final static class 箭神 {

        public final static int 神弩手 = 3220004;// 技能描述： - 提高弩弓系武器的熟练度和攻击力. 必须持有弩弓的时候才适用.\n必要技能 : #c精准弩 20级#
        public final static int 冒险岛勇士 = 3221000;// 技能描述： - 一定时间内把组队成员的所有属性点提高一定的百分比.
        public final static int 穿透箭 = 3221001;// 技能描述： - 复活龙的灵魂射箭.穿越敌人时吸收能量,变的更强.
        public final static int 火眼晶晶 = 3221002;// 技能描述： - 赋予组队成员针对敌人寻找弱点并给予敌人致命伤的能力.
//public final static int 飞龙冲击波 = 3221003;// 技能描述： - 召唤龙的灵魂发射强有力的箭. 被箭射中的敌人被退后很远
        public final static int 冰凤凰 = 3221005;// 技能描述： - 一定时间内召唤冰属性的冰凤凰. 最多可攻击4只怪物.\n必要技能 : #c金鹰召唤 15级 以上#
        public final static int 刺眼箭 = 3221006;// 技能描述： - 瞄准敌人的眼睛,阻碍视野. 一定时间内降低敌人的命中率.
        public final static int 一击要害箭 = 3221007;// 技能描述： - 瞄准敌人的要害,一击打倒敌人.
        public final static int 勇士的意志 = 3221008;// 技能描述： - 从异常状态中恢复. 随着级上升,能恢复异常的能力也上升.. \n#c冷却时间 : 10分#
    }

    public final static class 飞侠 {

        public final static int 集中术 = 4000000;// 技能描述： - [最高等级 : 20]\n增加命中率和回避率.
        public final static int 远程暗器 = 4000001;// 技能描述： - [最高等级 : 8]\n增加拳套系武器的射程距离.\n必需技能 : #c集中术等级3以上#
        public final static int 诅咒术 = 4001002;// 技能描述： - [最高等级 : 20]\n降低怪物的物理攻击力和物理防御力，而且怪物停止攻击，对一个怪物不能使用两次.
        public final static int 隐身术 = 4001003;// 技能描述： - [最高等级 : 20]\n消费MP, 能够隐身，不会受到怪物的攻击但也无法攻击怪物.\n必需技能 : #c诅咒术等级3以上#
        public final static int 二连击 = 4001334;// 技能描述： - [最高等级 : 20]\n用短刀连续攻击怪物两次.
        public final static int 双飞斩 = 4001344;// 技能描述： - [最高等级 : 20]\n使用飞镖对怪物连续攻击2次，攻击力受LUK数值的影响。
    }

    public final static class 刺客 {

        public final static int 精准暗器 = 4100000;// 技能描述： - [最高等级 : 20]\n增加使用拳套系武器的命中率及熟练度。(必须装备飞镖)
        public final static int 强力投掷 = 4100001;// 技能描述： - [最高等级 : 30]\n发挥更强力的攻击。(装备拳套系类武器)\n必需技能 : #c精准暗器等级5以上#
        public final static int 恢复术 = 4100002;// 技能描述： - [最高等级 : 20]\n在梯子和绳索上也能恢复HP/ MP.
        public final static int 快速暗器 = 4101003;// 技能描述： - [最高等级 : 20]\n消费HP,MP,一定时间内攻击速度提升一个等级。(必需装备拳套，使用飞镖)
        public final static int 轻功 = 4101004;// 技能描述： - [最高等级 : 20]\n一定时间内提高所有队员的移动速度和跳跃力.
        public final static int 生命吸收 = 4101005;// 技能描述： - [最高等级 : 30]\n按对怪物的伤害值的一定比例恢复HP。(吸收值以最大HP的50%为上限)\n必需技能 : #c恢复术等级3以上#
    }

    public final static class 无影人 {

        public final static int 药剂精通 = 4110000;// 技能描述： - [最高等级:20]\n提升使用各种恢复药水的效果，对按百分比恢复的道具不适用。
        public final static int 聚财术 = 4111001;// 技能描述： - [最高等级:20]\n一定时间内，全队获得更多金币。
        public final static int 影分身 = 4111002;// 技能描述： - [最高等级:30]\n召唤影分身。没有HP,和角色做一样的动作，会自动消失。
        public final static int 影网术 = 4111003;// 技能描述： - [最高等级:20]\n以自身的影子做成蜘蛛网，缠住6个以下的多个怪物。被缠住的怪物无法动弹。
        public final static int 金钱攻击 = 4111004;// 技能描述： - [最高等级:30]\n使用金币进行攻击，按投掷金币的比例伤害怪物。是无视物理防御或魔法防御的攻击。\n必需技能 : #c聚财术等级5以上#
        public final static int 多重飞镖 = 4111005;// 技能描述： - [最高等级:30]\n消耗MP制作大飞镖，攻击怪物。
        public final static int 二段跳 = 4111006;// 技能描述： - [最高等级:20]\n跳跃后在空中使用的话，能再一次跳跃。技能点上升跳跃力也会上升。\n必需技能 : #c多重飞镖等级5以上#
    }

    public final static class 隐士 {

        public final static int 假动作 = 4120002;// 技能描述： - 以极快的反射神经躲避敌人的攻击.
        public final static int 武器用毒液 = 4120005;// 技能描述： - 在飞镖上涂抹毒药攻击敌人使它一定几率中毒受持续伤害.最多可重复3次,敌人的HP不会掉落1以下.
        public final static int 冒险岛勇士 = 4121000;// 技能描述： - 一定时间内把组队成员的所有属性点提高一定的百分比.
        public final static int 挑衅 = 4121003;// 技能描述： - 最多可以把6只敌人陷入挑衅状态.随着敌人的防御力上升敌人掉落的经验值和物品掉落率上升.\n必需技能: #c假动作等级10以上#
        public final static int 忍者伏击 = 4121004;// 技能描述： - [最高等级 : 30]\n给一定范围内的敌人持续的伤害.一次不能攻击6只以上,HP不掉到1以下.\n必要技能 : #c假动作等级5以上#
        public final static int 暗器伤人 = 4121006;// 技能描述： - 一下子消耗飞镖200个后,一定时间内可以不消耗飞镖攻击敌人.
        public final static int 三连环光击破 = 4121007;// 技能描述： - 一下子扔3个飞镖攻击.
        public final static int 忍者冲击 = 4121008;// 技能描述： - 隐藏的忍者快速旋转使敌人左右推开.
        public final static int 勇士的意志 = 4121009;// 技能描述： - 从异常状态中恢复. 随着级上升,能恢复异常的能力也上升.. \n#c冷却时间 : 10分#
    }

    public final static class 侠客 {

        public final static int 精准短刀 = 4200000;// 技能描述： - [最高等级 : 20]\n增加使用短刀系武器的命中率及熟练度。(装备短刀，拳刃)
        public final static int 恢复术 = 4200001;// 技能描述： - [最高等级 : 20]\n能更多恢复HP/ MP.
        public final static int 快速短刀 = 4201002;// 技能描述： - [最高等级 : 20]\n攻击速度提升一个等级.(装备短刀，拳刃)\n必需技能 : #c精准短刀等级5以上#
        public final static int 轻功 = 4201003;// 技能描述： - [最高等级 : 20]\n提高所有队员的移动速度和跳跃力.
        public final static int 神通术 = 4201004;// 技能描述： - [最高等级 : 30]\n攻击时，根据一定概率获取怪物身上的道具。\n必需技能 : #c轻功等级5以上#
        public final static int 回旋斩 = 4201005;// 技能描述： - [最高等级 : 30]\n消费MP，最多连续攻击6次.
    }

    public final static class 独行客 {

        public final static int 强化盾 = 4210000;// 技能描述： - [最高等级:20]\n提高盾牌的物理防御力,必须装备盾牌。
        public final static int 转化术 = 4211001;// 技能描述： - [最高等级:30]\n消耗MP恢复HP。只有HP在一半以下才可以使用, 如果中途做动作或遭受攻击就停止。
        public final static int 落叶斩 = 4211002;// 技能描述： - [最高等级:30]\n瞬间攻击一个怪物，一定几率使其昏迷。
        public final static int 敛财术 = 4211003;// 技能描述： - [最高等级:20]\n攻击怪物时候让金币掉落。掉落金币数量随技能等级和伤害增加而增加。\n必需技能 : #c金钱炸弹等级3以上#
        public final static int 分身术 = 4211004;// 技能描述： - [最高等级:30]\n召唤分身，攻击周围怪物。最多攻击6个怪物。
        public final static int 金钱护盾 = 4211005;// 技能描述： - [最高等级:20]\n用金币代替伤害的50%。每次收到伤害时根据比例消耗金币。\n必需技能 : #c转化术等级3以上#
        public final static int 金钱炸弹 = 4211006;// 技能描述： - [最高等级:30]\n引爆前方掉落在地上的金币来攻击怪物，只能引爆属于自己的金币。
    }

    public final static class 侠盗 {

        public final static int 假动作 = 4220002;// 技能描述： - 以极快的反射神经躲避敌人的攻击.
        public final static int 武器用毒液 = 4220005;// 技能描述： - 在短剑上涂抹毒药攻击敌人,使它一定的几率陷入中毒状态受持续伤害.最多可重复3次,敌人的HP不会掉到1以下.
        public final static int 冒险岛勇士 = 4221000;// 技能描述： - 一定时间内把组队成员的所有属性点提高一定的百分比.
        public final static int 暗杀 = 4221001;// 技能描述： - 用隐身悄悄地靠近敌人附近后，突然攻击4次敌人的要害.最后一击以一定几率给敌人致命伤.
        public final static int 挑衅 = 4221003;// 技能描述： - 最多可以挑衅6只怪物.随着敌人的防御力上升敌人掉落的经验值和物品掉落率上升\n必需技能 : #c假动作等级10以上#
        public final static int 忍者伏击 = 4221004;// 技能描述： - 躲藏的同伴突然出现在一定时间内持续攻击敌人.\n一次无法攻击6只以上,HP不会掉到1以下.\n必要技能 : #c假动作 5级 以上#
        public final static int 烟幕弹 = 4221006;// 技能描述： - 为了从危险中迅速逃脱仍烟幕弹.烟幕弹内的组队成员在烟幕弹状态下不受敌人的伤害.\n#c冷却时间 : 10分#
        public final static int 一出双击 = 4221007;// 技能描述： - 用迅速的速度砍2次多数敌人.一定几率使敌人眩晕.
        public final static int 勇士的意志 = 4221008;// 技能描述： - 从异常状态中恢复. 随着级上升,能恢复异常的能力也上升.. \n#c冷却时间 : 10分#
    }

    public final static class 海盗 {

        public final static int 快动作 = 5000000;// 技能描述： - [最高等级 : 20]\n增加命中率和回避率.
        public final static int 百裂拳 = 5001001;// 技能描述： - [最高等级 : 20]\n消耗MP，用拳头快速攻击敌人.
        public final static int 半月踢 = 5001002;// 技能描述： - [最高等级 : 20]\n后旋跳，用腿给周围多数的敌人击退攻击.
        public final static int 双弹射击 = 5001003;// 技能描述： - [最高等级 : 20]\n同时发射二颗子弹，攻击敌人.
        public final static int 疾驰 = 5001005;// 技能描述： - [最高等级 : 10]\n连续两次敲打左右方向键中的某一个，瞬间提高移动速度和跳跃力.
    }

    public final static class 拳手 {

        public final static int 强体术 = 5100000;// 技能描述： - [最高等级 : 10]\n升级时，最大HP上使用AP时，提高最大Hp的增加量.
        public final static int 精准拳 = 5100001;// 技能描述： - [最高等级 : 20]\n提高拳甲系武器的熟练度和命中率。只有佩戴拳甲时才可适用.
        public final static int 回马 = 5101002;// 技能描述： - [最高等级 : 20]\n快速滑向后方，给与背后多数的敌人伤害. 
        public final static int 升龙连击 = 5101003;// 技能描述： - [最高等级 : 20]\n用拳头二度连续攻击，使敌人重伤.
        public final static int 贯骨击 = 5101004;// 技能描述： - [最高等级 : 20]\n向前方的多数敌人施加拳击.
        public final static int 生命分流 = 5101005;// 技能描述： - [最高等级 : 10]\n消耗自己的HP使之恢复MP.
        public final static int 急速拳 = 5101006;// 技能描述： - [最高等级 : 20]\n消耗HP,MP，一定时间内使拳的攻击速度提升。只有佩戴指节时才可发动技能.\n必要技能 : #c精准拳5级以上
        public final static int 橡木伪装 = 5101007;// 技能描述： - [最高等级 : 10]\n为了不受怪物攻击地移动，戴上橡木桶变装。有时可能会被好奇的怪物们发现，所以要格外小心。俯卧时被发现的概率减少.
    }

    public final static class 火枪手 {

        public final static int 精准枪 = 5200000;// 技能描述： - [最高等级 : 20]\n提升枪系武器的熟练度和命中率。只适用于有装备枪的时候。
        public final static int 快枪手 = 5201001;// 技能描述： - [最高等级 : 20]\n极短的时间内开枪，攻击多数敌人。
        public final static int 投弹攻击 = 5201002;// 技能描述： - [最高等级 : 20]\n扔炸弹攻击敌人，根据按键时间决定射程。
        public final static int 速射 = 5201003;// 技能描述： - [最高等级 : 20]\n消耗HP,MP，在一定时间内提升枪的攻击速度。只适用于装备枪的时候。\n必要技能 : #c精准枪5级以上#
        public final static int 迷惑射击 = 5201004;// 技能描述： - [最高等级 : 20]\n假装射击的同时发射迷惑旗，使多数怪物们惊慌。同时能攻击3只以下的怪物。
        public final static int 轻羽鞋 = 5201005;// 技能描述： - [最高等级 : 10]\n比目前的跳跃滞空时间更久的跳跃。
        public final static int 激退射杀 = 5201006;// 技能描述： - [最高等级 : 20]\n利用枪的后座力，一边射击，一边向后方移动。\n必要技能 : #c轻羽鞋5级以上#
    }

    public final static class 斗士 {

        public final static int 迷惑攻击 = 5110000;// 技能描述： - [最高等级 : 20]\n攻击迷惑状态的敌人时，以一定的概率出现爆击。
        public final static int 能量获得 = 5110001;// 技能描述： - [最高等级 : 40]\n攻击时获得一定量的能量补充，能量充沛时就会自动启动攻击和阿基里斯效果使之可以使用利用能量的技能。
        public final static int 能量爆破 = 5111002;// 技能描述： - [最高等级 : 30]\n散发能量，使近距离的多数怪物收到伤害。只有 #c能量充沛#的状态下才可以使用。\n必要技能 : #c能量获得1级以上#
        public final static int 能量耗转 = 5111004;// 技能描述： - [最高等级 : 20]\n利用能量，使敌人的一些HP变为自己的HP。只有#c能量充沛#的状态下才可以使用。\n必要技能 : #c能量获得1级以上#
        public final static int 超人变形 = 5111005;// 技能描述： - [最高等级 : 20]\n120秒 期间会变成超人状态. \n可使用的技能: 碎石乱击, 急速拳, 能量爆破, 能量耗转, 冒险岛勇士, 勇士的意志, 极速领域, 伺机待发
        public final static int 碎石乱击 = 5111006;// 技能描述： - [最高等级 : 30]\n猛烈地撞击大地引起冲击，给多数的怪物伤害。只有在 #c超人变形, 超级变身# 状态下使用.\n必要技能 : #c超人变形 1级以上#
    }

    public final static class 大副 {

        public final static int 三连射杀 = 5210000;// 技能描述： - [最高等级 : 20]\n双弹射杀使用时的弹数和攻击力增加。\n必要技能 : #c双弹射击 20等级#   
        public final static int 章鱼炮台 = 5211001;// 技能描述： - [最高等级 : 30]\n召唤帮助攻击的章鱼。但召唤的章鱼不会动。\n#c冷却时间 : 10秒#
        public final static int 海鸥空袭 = 5211002;// 技能描述： - [最高等级 : 30]\n召唤能向敌人扔炸弹的海鸥。海鸥一旦发现敌人就会仍炸弹。\n#c冷却时间 : 5秒#
        public final static int 烈焰喷射 = 5211004;// 技能描述： - [最高等级 : 30]\n给近距离的敌人火属性攻击。受到攻击的敌人在一定时间受到连续伤害。
        public final static int 寒冰喷射 = 5211005;// 技能描述： - [最高等级 : 30]\n给近距离的敌人冷属性攻击。受到攻击的敌人在一定时间内处于结冰状态。
        public final static int 导航 = 5211006;// 技能描述： - [最高等级 : 30]\n向敌人派遣瞄准用鹦鹉。以后所有的攻击都指向鹦鹉跟随的敌人。
    }

    public final static class 冲锋队长 {

        public final static int 冒险岛勇士 = 5121000;// 技能描述： - 一定时间内把组队成员的所有属性点提高一定的百分比。
        public final static int 潜龙出渊 = 5121001;// 技能描述： - 召唤沉睡的龙，给与多数的敌人伤害。
        public final static int 超能量 = 5121002;// 技能描述： - 爆发聚集的能量攻击敌人。如果周围有其他怪物，超能量会转移给与怪物累计伤害。只能#c能量充沛#的状态下使用。\n必要技能 : #c能量获得1级以上#
        public final static int 超级变身 = 5121003;// 技能描述： - 120秒内变成超级状态，变身时外形会变，且可以使用超级变身特有的技能。\n可使用技能 : 碎石乱击, 金手指, 索命, 急速拳, 能量爆破, 能量耗转, 冒险岛勇士, 勇士的意志, 极速领域, 伺机待发\n必要技能 : #c超人变形 20级以上#
        public final static int 金手指 = 5121004;// 技能描述： - 超越极限的快速攻击，给一个敌人带来大的伤害，只能在超级变身状态下使用，具有眩晕效果。只有在 #c超级变身# 状态下才可以使用.\n必要技能 : #c超级变身 1级以上#
        public final static int 索命 = 5121005;// 技能描述： - 给与远距离怪物伤害，并身体向前移动，只能在超级变身状态下使用。只有在 #c超级变身# 状态下使用。\n必要技能 : #c超级变身 1级以上#
        public final static int 光速拳 = 5121007;// 技能描述： - 极快的速度在近距离的一个怪物攻击6下。
        public final static int 勇士的意志 = 5121008;// 技能描述： - 从异常状态中恢复. 随着等级上升,能恢复异常的能力也上升。 \n#c冷却时间 : 10分#
        public final static int 极速领域 = 5121009;// 技能描述： - 消耗HP,MP,一定时间内使武器的攻击速度上升。可与其他武器速度提升技能重复，组队成员全部能收到这些技能的效果。\n必要技能 : #c急速拳 20级以上#
        public final static int 伺机待发 = 5121010;// 技能描述： - 初始化自己和团队使用的全部的技能和冷却时间。只是本技能的冷却时间不能初始化。
    }

    public final static class 船长 {

        public final static int 冒险岛勇士 = 5221000;// 技能描述： - 一定时间内把组队成员的所有属性点提高一定的百分比。
        public final static int 属性强化 = 5220001;// 技能描述： - 提升烈焰喷射和寒冰喷射技能的伤害值。
        public final static int 超级章鱼炮台 = 5220002;// 技能描述： - 召唤一只章鱼提升连射速度和伤害值。\n必要技能 : #c章鱼炮台30等级#
        public final static int 地毯式空袭 = 5221003;// 技能描述： - 召唤炸弹海鸥群的炸弹攻击，攻击6个以下的敌人。 \n必要技能 : #c海鸥空袭15级以上#
        public final static int 金属风暴 = 5221004;// 技能描述： - 发射非常快的子弹。摁住此技能键的状态可持续。\n必要技能 : #c三连射杀 20等级#
        public final static int 武装 = 5221006;// 技能描述： - 登上海盗船，移动速度减慢，不能爬绳爬梯子，但是相对防御力变强，受到伤害时耐久度减少，耐久度为0时，不能登海盗船。\n可使用技能 : 武装, 投弹攻击, 速射, 烈焰喷射, 寒冰喷射, 海鸥空袭, 章鱼炮台, 冒险岛勇士, 勇士的意志 
        public final static int 急速射 = 5221007;// 技能描述： - 连续发射数发炮弹。只能是“武装”技能使用后的状态下才可使用。\n必要技能 : #c武装 1级以上#
        public final static int 重量炮击 = 5221008;// 技能描述： - 向敌人发射结实的炮弹。只能是“武装”技能使用后的状态下才可使用。\n必要技能 : #c武装 1级以上#
        public final static int 心灵控制 = 5221009;// 技能描述： - 诱惑怪物，使怪物在一定时间内攻击其他怪物。 
        public final static int 勇士的意志 = 5221010;// 技能描述： - 从异常状态中恢复. 随着等级上升,能恢复异常的能力也上升。\n#c冷却时间 : 10分#
        public final static int 导航辅助 = 5220011;// 技能描述： - 给与中招的怪物更多的伤害。\n必要技能 : #c导航 30等级#
    }
}
