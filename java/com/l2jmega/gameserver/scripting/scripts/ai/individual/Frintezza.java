package com.l2jmega.gameserver.scripting.scripts.ai.individual;
 
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.l2jmega.commons.concurrent.ThreadPool;
import com.l2jmega.commons.random.Rnd;

import com.l2jmega.Config;
import com.l2jmega.gameserver.data.DoorTable;
import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.instancemanager.GrandBossManager;
import com.l2jmega.gameserver.instancemanager.ZoneManager;
import com.l2jmega.gameserver.model.L2CommandChannel;
import com.l2jmega.gameserver.model.L2Party;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.actor.Attackable;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.ai.CtrlIntention;
import com.l2jmega.gameserver.model.actor.instance.GrandBoss;
import com.l2jmega.gameserver.model.actor.instance.Monster;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.type.L2BossZone;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.AbstractNpcInfo;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.network.serverpackets.Earthquake;
import com.l2jmega.gameserver.network.serverpackets.MagicSkillCanceld;
import com.l2jmega.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jmega.gameserver.network.serverpackets.NpcSay;
import com.l2jmega.gameserver.network.serverpackets.PlaySound;
import com.l2jmega.gameserver.network.serverpackets.SocialAction;
import com.l2jmega.gameserver.network.serverpackets.SpecialCamera;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.scripting.scripts.ai.L2AttackableAIScript;
import com.l2jmega.gameserver.skills.AbnormalEffect;
import com.l2jmega.gameserver.templates.StatsSet;
 
public class Frintezza extends L2AttackableAIScript
{
    private static final int[][] _invadeLoc =
    {
        {
            174984,
            -75160,
            -5104
        },
        {
            174984,
            -75160,
            -5104
        },
        {
            173160,
            -75208,
            -5104
        },
        {
            173032,
            -76808,
            -5104
        },
        {
            175000,
            -77144,
            -5104
        },
        {
            175464,
            -76104,
            -5104
        }
    };
    private static final int[][] _skill =
    {
        {
            5015,
            1,
            5000
        },
        {
            5015,
            4,
            5000
        },
        {
            5015,
            2,
            5000
        },
        {
            5015,
            5,
            5000
        },
        {
            5018,
            1,
            10000
        },
        {
            5016,
            1,
            5000
        },
        {
            5015,
            3,
            5000
        },
        {
            5015,
            6,
            5000
        },
        {
            5018,
            2,
            10000
        },
        {
            5019,
            1,
            10000
        },
        {
            5016,
            1,
            5000
        }
    };
    private static final int[][] _mobLoc =
    {
        {
            18328,
            172894,
            -76019,
            -5099,
            243
        },
        {
            18328,
            174095,
            -77279,
            -5099,
            -5099
        },
        {
            18328,
            174111,
            -74833,
            -5099,
            49043
        },
        {
            18328,
            175344,
            -76042,
            -5099,
            32847
        },
        {
            18330,
            173489,
            -76227,
            -5099,
            63565
        },
        {
            18330,
            173498,
            -75724,
            -5099,
            58498
        },
        {
            18330,
            174365,
            -76745,
            -5099,
            22424
        },
        {
            18330,
            174570,
            -75584,
            -5099,
            31968
        },
        {
            18330,
            174613,
            -76179,
            -5099,
            31471
        },
        {
            18332,
            173620,
            -75981,
            -5099,
            4588
        },
        {
            18332,
            173630,
            -76340,
            -5099,
            62454
        },
        {
            18332,
            173755,
            -75613,
            -5099,
            57892
        },
        {
            18332,
            173823,
            -76688,
            -5099,
            2411
        },
        {
            18332,
            174000,
            -75411,
            -5099,
            54718
        },
        {
            18332,
            174487,
            -75555,
            -5099,
            33861
        },
        {
            18332,
            174517,
            -76471,
            -5099,
            21893
        },
        {
            18332,
            174576,
            -76122,
            -5099,
            31176
        },
        {
            18332,
            174600,
            -75841,
            -5099,
            35927
        },
        {
            18329,
            173481,
            -76043,
            -5099,
            61312
        },
        {
            18329,
            173539,
            -75678,
            -5099,
            59524
        },
        {
            18329,
            173584,
            -76386,
            -5099,
            3041
        },
        {
            18329,
            173773,
            -75420,
            -5099,
            51115
        },
        {
            18329,
            173777,
            -76650,
            -5099,
            12588
        },
        {
            18329,
            174585,
            -76510,
            -5099,
            21704
        },
        {
            18329,
            174623,
            -75571,
            -5099,
            40141
        },
        {
            18329,
            174744,
            -76240,
            -5099,
            29202
        },
        {
            18329,
            174769,
            -75895,
            -5099,
            29572
        },
        {
            18333,
            173861,
            -76011,
            -5099,
            383
        },
        {
            18333,
            173872,
            -76461,
            -5099,
            8041
        },
        {
            18333,
            173898,
            -75668,
            -5099,
            51856
        },
        {
            18333,
            174422,
            -75689,
            -5099,
            42878
        },
        {
            18333,
            174460,
            -76355,
            -5099,
            27311
        },
        {
            18333,
            174483,
            -76041,
            -5099,
            30947
        },
        {
            18331,
            173515,
            -76184,
            -5099,
            6971
        },
        {
            18331,
            173516,
            -75790,
            -5099,
            3142
        },
        {
            18331,
            173696,
            -76675,
            -5099,
            6757
        },
        {
            18331,
            173766,
            -75502,
            -5099,
            60827
        },
        {
            18331,
            174473,
            -75321,
            -5099,
            37147
        },
        {
            18331,
            174493,
            -76505,
            -5099,
            34503
        },
        {
            18331,
            174568,
            -75654,
            -5099,
            41661
        },
        {
            18331,
            174584,
            -76263,
            -5099,
            31729
        },
        {
            18339,
            173892,
            -81592,
            -5099,
            50849
        },
        {
            18339,
            173958,
            -81820,
            -5099,
            7459
        },
        {
            18339,
            174128,
            -81805,
            -5099,
            21495
        },
        {
            18339,
            174245,
            -81566,
            -5099,
            41760
        },
        {
            18334,
            173264,
            -81529,
            -5099,
            1646
        },
        {
            18334,
            173265,
            -81656,
            -5099,
            441
        },
        {
            18334,
            173267,
            -81889,
            -5099,
            0
        },
        {
            18334,
            173271,
            -82015,
            -5099,
            65382
        },
        {
            18334,
            174867,
            -81655,
            -5099,
            32537
        },
        {
            18334,
            174868,
            -81890,
            -5099,
            32768
        },
        {
            18334,
            174869,
            -81485,
            -5099,
            32315
        },
        {
            18334,
            174871,
            -82017,
            -5099,
            33007
        },
        {
            18335,
            173074,
            -80817,
            -5099,
            8353
        },
        {
            18335,
            173128,
            -82702,
            -5099,
            5345
        },
        {
            18335,
            173181,
            -82544,
            -5099,
            65135
        },
        {
            18335,
            173191,
            -80981,
            -5099,
            6947
        },
        {
            18335,
            174859,
            -80889,
            -5099,
            24103
        },
        {
            18335,
            174924,
            -82666,
            -5099,
            38710
        },
        {
            18335,
            174947,
            -80733,
            -5099,
            22449
        },
        {
            18335,
            175096,
            -82724,
            -5099,
            42205
        },
        {
            18336,
            173435,
            -80512,
            -5099,
            65215
        },
        {
            18336,
            173440,
            -82948,
            -5099,
            417
        },
        {
            18336,
            173443,
            -83120,
            -5099,
            1094
        },
        {
            18336,
            173463,
            -83064,
            -5099,
            286
        },
        {
            18336,
            173465,
            -80453,
            -5099,
            174
        },
        {
            18336,
            173465,
            -83006,
            -5099,
            2604
        },
        {
            18336,
            173468,
            -82889,
            -5099,
            316
        },
        {
            18336,
            173469,
            -80570,
            -5099,
            65353
        },
        {
            18336,
            173469,
            -80628,
            -5099,
            166
        },
        {
            18336,
            173492,
            -83121,
            -5099,
            394
        },
        {
            18336,
            173493,
            -80683,
            -5099,
            0
        },
        {
            18336,
            173497,
            -80510,
            -5099,
            417
        },
        {
            18336,
            173499,
            -82947,
            -5099,
            0
        },
        {
            18336,
            173521,
            -83063,
            -5099,
            316
        },
        {
            18336,
            173523,
            -82889,
            -5099,
            128
        },
        {
            18336,
            173524,
            -80627,
            -5099,
            65027
        },
        {
            18336,
            173524,
            -83007,
            -5099,
            0
        },
        {
            18336,
            173526,
            -80452,
            -5099,
            64735
        },
        {
            18336,
            173527,
            -80569,
            -5099,
            65062
        },
        {
            18336,
            174602,
            -83122,
            -5099,
            33104
        },
        {
            18336,
            174604,
            -82949,
            -5099,
            33184
        },
        {
            18336,
            174609,
            -80514,
            -5099,
            33234
        },
        {
            18336,
            174609,
            -80684,
            -5099,
            32851
        },
        {
            18336,
            174629,
            -80627,
            -5099,
            33346
        },
        {
            18336,
            174632,
            -80570,
            -5099,
            32896
        },
        {
            18336,
            174632,
            -83066,
            -5099,
            32768
        },
        {
            18336,
            174635,
            -82893,
            -5099,
            33594
        },
        {
            18336,
            174636,
            -80456,
            -5099,
            32065
        },
        {
            18336,
            174639,
            -83008,
            -5099,
            33057
        },
        {
            18336,
            174660,
            -80512,
            -5099,
            33057
        },
        {
            18336,
            174661,
            -83121,
            -5099,
            32768
        },
        {
            18336,
            174663,
            -82948,
            -5099,
            32768
        },
        {
            18336,
            174664,
            -80685,
            -5099,
            32676
        },
        {
            18336,
            174687,
            -83008,
            -5099,
            32520
        },
        {
            18336,
            174691,
            -83066,
            -5099,
            32961
        },
        {
            18336,
            174692,
            -80455,
            -5099,
            33202
        },
        {
            18336,
            174692,
            -80571,
            -5099,
            32768
        },
        {
            18336,
            174693,
            -80630,
            -5099,
            32994
        },
        {
            18336,
            174693,
            -82889,
            -5099,
            32622
        },
        {
            18337,
            172837,
            -82382,
            -5099,
            58363
        },
        {
            18337,
            172867,
            -81123,
            -5099,
            64055
        },
        {
            18337,
            172883,
            -82495,
            -5099,
            64764
        },
        {
            18337,
            172916,
            -81033,
            -5099,
            7099
        },
        {
            18337,
            172940,
            -82325,
            -5099,
            58998
        },
        {
            18337,
            172946,
            -82435,
            -5099,
            58038
        },
        {
            18337,
            172971,
            -81198,
            -5099,
            14768
        },
        {
            18337,
            172992,
            -81091,
            -5099,
            9438
        },
        {
            18337,
            173032,
            -82365,
            -5099,
            59041
        },
        {
            18337,
            173064,
            -81125,
            -5099,
            5827
        },
        {
            18337,
            175014,
            -81173,
            -5099,
            26398
        },
        {
            18337,
            175061,
            -82374,
            -5099,
            43290
        },
        {
            18337,
            175096,
            -81080,
            -5099,
            24719
        },
        {
            18337,
            175169,
            -82453,
            -5099,
            37672
        },
        {
            18337,
            175172,
            -80972,
            -5099,
            32315
        },
        {
            18337,
            175174,
            -82328,
            -5099,
            41760
        },
        {
            18337,
            175197,
            -81157,
            -5099,
            27617
        },
        {
            18337,
            175245,
            -82547,
            -5099,
            40275
        },
        {
            18337,
            175249,
            -81075,
            -5099,
            28435
        },
        {
            18337,
            175292,
            -82432,
            -5099,
            42225
        },
        {
            18338,
            173014,
            -82628,
            -5099,
            11874
        },
        {
            18338,
            173033,
            -80920,
            -5099,
            10425
        },
        {
            18338,
            173095,
            -82520,
            -5099,
            49152
        },
        {
            18338,
            173115,
            -80986,
            -5099,
            9611
        },
        {
            18338,
            173144,
            -80894,
            -5099,
            5345
        },
        {
            18338,
            173147,
            -82602,
            -5099,
            51316
        },
        {
            18338,
            174912,
            -80825,
            -5099,
            24270
        },
        {
            18338,
            174935,
            -80899,
            -5099,
            18061
        },
        {
            18338,
            175016,
            -82697,
            -5099,
            39533
        },
        {
            18338,
            175041,
            -80834,
            -5099,
            25420
        },
        {
            18338,
            175071,
            -82549,
            -5099,
            39163
        },
        {
            18338,
            175154,
            -82619,
            -5099,
            36345
        }
    };
    private static final int SCARLET1 = 29046;
    private static final int SCARLET2 = 29047;
    private static final int FRINTEZZA = 29045;
    private static final int GUIDE = 32011;
    private static final int CUBE = 29061;
    
    private static final byte DORMANT = 0;
    private static final byte WAITING = 1;
    
    private static final byte FIGHTING = 2;
    private static final byte DEAD = 3;
    
    private static long _LastAction = 0L;
    private static int _Angle = 0;
    private static int _Heading = 0;
    private static int _LocCycle = 0;
    private static int _Bomber = 0;
    private static int _CheckDie = 0;
    private static int _OnCheck = 0;
    private static int _OnSong = 0;
    private static int _Abnormal = 0;
    private static int _OnMorph = 0;
    private static int _Scarlet_x = 0;
    private static int _Scarlet_y = 0;
    private static int _Scarlet_z = 0;
    private static int _Scarlet_h = 0;
    private static int _SecondMorph = 0;
    private static int _ThirdMorph = 0;
    private static int _KillHallAlarmDevice = 0;
    private static int _KillDarkChoirPlayer = 0;
    private static int _KillDarkChoirCaptain = 0;
    
    private static L2BossZone _Zone;
    private GrandBoss frintezza;
    private GrandBoss weakScarlet;
    private GrandBoss strongScarlet;
    private GrandBoss activeScarlet;
    private Monster demon1;
    private Monster demon2;
    private Monster demon3;
    private Monster demon4;
    private Monster portrait1;
    private Monster portrait2;
    private Monster portrait3;
    private Monster portrait4;
    private Npc _frintezzaDummy;
    private Npc _overheadDummy;
    private Npc _portraitDummy1;
    private Npc _portraitDummy3;
    private Npc _scarletDummy;
    private static List<Player> _PlayersInside = new CopyOnWriteArrayList<>();
    private static List<Npc> _Room1Mobs = new CopyOnWriteArrayList<>();
    private static List<Npc> _Room2Mobs = new CopyOnWriteArrayList<>();
    private static List<Attackable> Minions = new CopyOnWriteArrayList<>();
    
    public Frintezza()
    {
        super("ai/individual");
    }
    
    @Override
    protected void registerNpcs()
    {
        int[] mob =
        {
            SCARLET1,
            SCARLET2,
            FRINTEZZA,
            18328,
            18329,
            18330,
            18331,
            18332,
            18333,
            18334,
            18335,
            18336,
            18337,
            18338,
            18339,
            29048,
            29049,
            29050,
            29051
        };
        
        _Zone = ZoneManager.getInstance().getZoneById(110011, L2BossZone.class);
        addAttackId(mob);
        addKillId(mob);
        addStartNpc(new int[]
        {
            GUIDE
        });
        addTalkId(new int[]
        {
            GUIDE
        });
        addStartNpc(new int[]
        {
            CUBE
        });
        addTalkId(new int[]
        {
            CUBE
        });
        StatsSet info = GrandBossManager.getInstance().getStatsSet(FRINTEZZA);
        int status = GrandBossManager.getInstance().getBossStatus(FRINTEZZA);
        if (status == 3)
        {
            long temp = info.getLong("respawn_time") - System.currentTimeMillis();
            if (temp > 0L)
            {
                startQuestTimer("frintezza_unlock", temp, null, null, false);
            }
            else
            {
                GrandBossManager.getInstance().setBossStatus(FRINTEZZA,DORMANT);
            }
        }
        else if (status != 0)
        {
            GrandBossManager.getInstance().setBossStatus(FRINTEZZA,DORMANT);
        }
    }
    
    @Override
    public String onAdvEvent(String event, Npc npc, Player player)
    {
        long temp = 0L;
        if (event.equalsIgnoreCase("waiting"))
        {
        	DoorTable.getInstance().getDoor(25150046).closeMe();
            startQuestTimer("close", 27000L, npc, null, false);
            startQuestTimer("camera_1", 30000L, npc, null, false);
            _Zone.broadcastPacket(new Earthquake(174232, -88020, -5116, 45, 27));
        }
        else if (event.equalsIgnoreCase("room1_spawn"))
        {
            CreatureSay cs = new CreatureSay(0, 1, "Hall Alarm Device", "Intruders! Sound the alarm!");
            _Zone.broadcastPacket(cs);
            for (int i = 0; i <= 17; i++)
            {
                Npc mob = addSpawn(_mobLoc[i][0], _mobLoc[i][1], _mobLoc[i][2], _mobLoc[i][3], _mobLoc[i][4], false, 0L, false);
                _Room1Mobs.add(mob);
            }
        }
        else if (event.equalsIgnoreCase("room1_spawn2"))
        {
            for (int i = 18; i <= 26; i++)
            {
                Npc mob = addSpawn(_mobLoc[i][0], _mobLoc[i][1], _mobLoc[i][2], _mobLoc[i][3], _mobLoc[i][4], false, 0L, false);
                _Room1Mobs.add(mob);
            }
        }
        else if (event.equalsIgnoreCase("room1_spawn3"))
        {
            for (int i = 27; i <= 32; i++)
            {
                Npc mob = addSpawn(_mobLoc[i][0], _mobLoc[i][1], _mobLoc[i][2], _mobLoc[i][3], _mobLoc[i][4], false, 0L, false);
                _Room1Mobs.add(mob);
            }
        }
        else if (event.equalsIgnoreCase("room1_spawn4"))
        {
            for (int i = 33; i <= 40; i++)
            {
                Npc mob = addSpawn(_mobLoc[i][0], _mobLoc[i][1], _mobLoc[i][2], _mobLoc[i][3], _mobLoc[i][4], false, 0L, false);
                _Room1Mobs.add(mob);
            }
        }
        else if (event.equalsIgnoreCase("room2_spawn"))
        {
            for (int i = 41; i <= 44; i++)
            {
                Npc mob = addSpawn(_mobLoc[i][0], _mobLoc[i][1], _mobLoc[i][2], _mobLoc[i][3], _mobLoc[i][4], false, 0L, false);
                _Room2Mobs.add(mob);
            }
        }
        else
        {
            if (event.equalsIgnoreCase("room2_spawn2"))
            {
                for (int i = 45; i <= 131; i++)
                {
                    Npc mob = addSpawn(_mobLoc[i][0], _mobLoc[i][1], _mobLoc[i][2], _mobLoc[i][3], _mobLoc[i][4], false, 0L, false);
                    _Room2Mobs.add(mob);
                }
            }
            else if (event.equalsIgnoreCase("room1_del"))
            {
                for (Npc mob : _Room1Mobs)
                {
                    if (mob != null)
                    {
                        mob.deleteMe();
                    }
                }
                _Room1Mobs.clear();
            }
            else if (event.equalsIgnoreCase("room2_del"))
            {
                for (Npc mob : _Room2Mobs)
                {
                    if (mob != null)
                    {
                        mob.deleteMe();
                    }
                }
                _Room2Mobs.clear();
            }
            else if (event.equalsIgnoreCase("room3_del"))
            {
                if (demon1 != null)
                {
                    demon1.deleteMe();
                }
                if (demon2 != null)
                {
                    demon2.deleteMe();
                }
                if (demon3 != null)
                {
                    demon3.deleteMe();
                }
                if (demon4 != null)
                {
                    demon4.deleteMe();
                }
                if (portrait1 != null)
                {
                    portrait1.deleteMe();
                }
                if (portrait2 != null)
                {
                    portrait2.deleteMe();
                }
                if (portrait3 != null)
                {
                    portrait3.deleteMe();
                }
                if (portrait4 != null)
                {
                    portrait4.deleteMe();
                }
                if (frintezza != null)
                {
                    frintezza.deleteMe();
                }
                if (weakScarlet != null)
                {
                    weakScarlet.deleteMe();
                }
                if (strongScarlet != null)
                {
                    strongScarlet.deleteMe();
                }
                demon1 = null;
                demon2 = null;
                demon3 = null;
                demon4 = null;
                portrait1 = null;
                portrait2 = null;
                portrait3 = null;
                portrait4 = null;
                frintezza = null;
                weakScarlet = null;
                strongScarlet = null;
                activeScarlet = null;
            }
            else if (event.equalsIgnoreCase("clean"))
            {
                _LastAction = 0L;
                _LocCycle = 0;
                _CheckDie = 0;
                _OnCheck = 0;
                _Abnormal = 0;
                _OnMorph = 0;
                _SecondMorph = 0;
                _ThirdMorph = 0;
                _KillHallAlarmDevice = 0;
                _KillDarkChoirPlayer = 0;
                _KillDarkChoirCaptain = 0;
                _PlayersInside.clear();
            }
            else
            {
                if (event.equalsIgnoreCase("close"))
                {
                    for (int i = 25150051; i <= 25150058; i++)
                    {
                        DoorTable.getInstance().getDoor(i).closeMe();
                    }
                    for (int i = 25150061; i <= 25150070; i++)
                    {
                        DoorTable.getInstance().getDoor(i).closeMe();
                    }
                    //DoorTable.getInstance().getDoor(25150042).closeMe();
                   // DoorTable.getInstance().getDoor(25150043).closeMe();
                   // DoorTable.getInstance().getDoor(25150045).closeMe();
                   // DoorTable.getInstance().getDoor(25150046).closeMe();
                }
                else if (event.equalsIgnoreCase("loc_check"))
                {
                    if (GrandBossManager.getInstance().getBossStatus(FRINTEZZA) == FIGHTING)
                    {
                        if (!_Zone.isInsideZone(npc))
                        {
                            npc.teleToLocation(174232, -88020, -5116, 0);
                        }
                        if ((npc.getX() < 176532) || (npc.getX() > 176532) || (npc.getY() < -90320) || (npc.getY() > -85720) || (npc.getZ() < -5130))
                        {
                            npc.teleToLocation(174232, -88020, -5116, 0);
                        }
                    }
                }
                else if (event.equalsIgnoreCase("camera_1"))
                {
                    GrandBossManager.getInstance().setBossStatus(FRINTEZZA, 2);
                    _frintezzaDummy = addSpawn(29052, 174240, -89805, -5022, 16048, false, 0L, false);
                    _frintezzaDummy.setIsInvul(false);
                    _frintezzaDummy.setIsImmobilized(false);
                    _overheadDummy = addSpawn(29052, 174232, -88020, -5110, 16384, false, 0L, false);
                    _overheadDummy.setIsInvul(false);
                    _overheadDummy.setIsImmobilized(false);
                    _overheadDummy.setCollisionHeight(600.0D);
                    _Zone.broadcastPacket(new AbstractNpcInfo.NpcInfo(_overheadDummy, null));
                    _portraitDummy1 = addSpawn(29052, 172450, -87890, -5110, 16048, false, 0L, false);
                    _portraitDummy1.setIsImmobilized(false);
                    _portraitDummy1.setIsInvul(false);
                    _portraitDummy3 = addSpawn(29052, 176012, -87890, -5110, 16048, false, 0L, false);
                    _portraitDummy3.setIsImmobilized(false);
                    _portraitDummy3.setIsInvul(false);
                    _scarletDummy = addSpawn(29053, 174232, -88020, -5110, 16384, false, 0L, false);
                    _scarletDummy.setIsInvul(false);
                    _scarletDummy.setIsImmobilized(false);
                    startQuestTimer("stop_pc", 0L, npc, null, false);
                    startQuestTimer("camera_2", 1000L, _overheadDummy, null, false);
                }
                else if (event.equalsIgnoreCase("camera_2"))
                {
                    _Zone.broadcastPacket(new SpecialCamera(_overheadDummy.getObjectId(), 0, 75, -89, 0, 100, 0, 0, 1, 0));
                    startQuestTimer("camera_2b", 0L, _overheadDummy, null, false);
                }
                else if (event.equalsIgnoreCase("camera_2b"))
                {
                    _Zone.broadcastPacket(new SpecialCamera(_overheadDummy.getObjectId(), 0, 75, -89, 0, 100, 0, 0, 1, 0));
                    startQuestTimer("camera_3", 0L, _overheadDummy, null, false);
                }
                else if (event.equalsIgnoreCase("camera_3"))
                {
                    _Zone.broadcastPacket(new SpecialCamera(_overheadDummy.getObjectId(), 300, 90, -10, 6500, 7000, 0, 0, 1, 0));
                    frintezza = ((GrandBoss) addSpawn(FRINTEZZA, 174240, -89805, -5022, 16048, false, 0L, false));
                    GrandBossManager.getInstance().addBoss(frintezza);
                    frintezza.setIsImmobilized(true);
                    frintezza.setIsInvul(true);
                    frintezza.disableAllSkills();
                    demon2 = ((Monster) addSpawn(29051, 175876, -88713, -5100, 28205, false, 0L, false));
                    demon2.setIsImmobilized(true);
                    demon2.disableAllSkills();
                    demon3 = ((Monster) addSpawn(29051, 172608, -88702, -5100, 64817, false, 0L, false));
                    demon3.setIsImmobilized(true);
                    demon3.disableAllSkills();
                    demon1 = ((Monster) addSpawn(29050, 175833, -87165, -5100, 35048, false, 0L, false));
                    demon1.setIsImmobilized(true);
                    demon1.disableAllSkills();
                    demon4 = ((Monster) addSpawn(29050, 172634, -87165, -5100, 57730, false, 0L, false));
                    demon4.setIsImmobilized(true);
                    demon4.disableAllSkills();
                    startQuestTimer("camera_4", 6500L, _overheadDummy, null, false);
                }
                else if (event.equalsIgnoreCase("camera_4"))
                {
                    _Zone.broadcastPacket(new SpecialCamera(_frintezzaDummy.getObjectId(), 1800, 90, 8, 6500, 7000, 0, 0, 1, 0));
                    startQuestTimer("camera_5", 900L, _frintezzaDummy, null, false);
                }
                else if (event.equalsIgnoreCase("camera_5"))
                {
                    _Zone.broadcastPacket(new SpecialCamera(_frintezzaDummy.getObjectId(), 140, 90, 10, 2500, 4500, 0, 0, 1, 0));
                    startQuestTimer("camera_5b", 4000L, _frintezzaDummy, null, false);
                }
                else if (event.equalsIgnoreCase("camera_5b"))
                {
                    _Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 40, 75, -10, 0, 1000, 0, 0, 1, 0));
                    startQuestTimer("camera_6", 0L, frintezza, null, false);
                }
                else if (event.equalsIgnoreCase("camera_6"))
                {
                    _Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 40, 75, -10, 0, 12000, 0, 0, 1, 0));
                    startQuestTimer("camera_7", 1350L, frintezza, null, false);
                }
                else if (event.equalsIgnoreCase("camera_7"))
                {
                    _Zone.broadcastPacket(new SocialAction(frintezza, 2));
                    startQuestTimer("camera_8", 7000L, frintezza, null, false);
                }
                else if (event.equalsIgnoreCase("camera_8"))
                {
                    startQuestTimer("camera_9", 1000L, frintezza, null, false);
                    _frintezzaDummy.deleteMe();
                    _frintezzaDummy = null;
                }
                else if (event.equalsIgnoreCase("camera_9"))
                {
                    _Zone.broadcastPacket(new SocialAction(demon2, 1));
                    _Zone.broadcastPacket(new SocialAction(demon3, 1));
                    startQuestTimer("camera_9b", 400L, frintezza, null, false);
                }
                else if (event.equalsIgnoreCase("camera_9b"))
                {
                    _Zone.broadcastPacket(new SocialAction(demon1, 1));
                    _Zone.broadcastPacket(new SocialAction(demon4, 1));
                    for (Creature pc : _Zone.getCharactersInside())
                    {
                        if ((pc instanceof Player))
                        {
                            if (pc.getX() < 174232)
                                pc.broadcastPacket(new SpecialCamera(_portraitDummy1.getObjectId(), 1000, 118, 0, 0, 1000, 0, 0, 1, 0));
                            else
                                pc.broadcastPacket(new SpecialCamera(_portraitDummy3.getObjectId(), 1000, 62, 0, 0, 1000, 0, 0, 1, 0));
                        }
                    }
                    startQuestTimer("camera_9c", 0L, frintezza, null, false);
                }
                else if (event.equalsIgnoreCase("camera_9c"))
                {
                    for (Creature pc : _Zone.getCharactersInside())
                    {
                        if ((pc instanceof Player))
                        {
                            if (pc.getX() < 174232)
                                pc.broadcastPacket(new SpecialCamera(_portraitDummy1.getObjectId(), 1000, 118, 0, 0, 10000, 0, 0, 1, 0));
                            else
                                pc.broadcastPacket(new SpecialCamera(_portraitDummy3.getObjectId(), 1000, 62, 0, 0, 10000, 0, 0, 1, 0));
                        }
                    }
                    startQuestTimer("camera_10", 2000L, frintezza, null, false);
                }
                else if (event.equalsIgnoreCase("camera_10"))
                {
                    _Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 240, 90, 0, 0, 1000, 0, 0, 1, 0));
                    startQuestTimer("camera_11", 0L, frintezza, null, false);
                }
                else if (event.equalsIgnoreCase("camera_11"))
                {
                    _Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 240, 90, 25, 5500, 10000, 0, 0, 1, 0));
                    _Zone.broadcastPacket(new SocialAction(frintezza, 3));
                    _portraitDummy1.deleteMe();
                    _portraitDummy3.deleteMe();
                    _portraitDummy1 = null;
                    _portraitDummy3 = null;
                    startQuestTimer("camera_12", 4500L, frintezza, null, false);
                }
                else if (event.equalsIgnoreCase("camera_12"))
                {
                    _Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 100, 195, 35, 0, 10000, 0, 0, 1, 0));
                    startQuestTimer("camera_13", 700L, frintezza, null, false);
                }
                else if (event.equalsIgnoreCase("camera_13"))
                {
                    _Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 100, 195, 35, 0, 10000, 0, 0, 1, 0));
                    startQuestTimer("camera_14", 1300L, frintezza, null, false);
                }
                else if (event.equalsIgnoreCase("camera_14"))
                {
                    _Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 120, 180, 45, 1500, 10000, 0, 0, 1, 0));
                    _Zone.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5006, 1, 34000, 0));
                    startQuestTimer("camera_16", 1500L, frintezza, null, false);
                }
                else if (event.equalsIgnoreCase("camera_16"))
                {
                    _Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 520, 135, 45, 8000, 10000, 0, 0, 1, 0));
                    startQuestTimer("camera_17", 7500L, frintezza, null, false);
                }
                else if (event.equalsIgnoreCase("camera_17"))
                {
                    _Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 1500, 110, 25, 10000, 13000, 0, 0, 1, 0));
                    startQuestTimer("camera_18", 9500L, frintezza, null, false);
                }
                else if (event.equalsIgnoreCase("camera_18"))
                {
                    _Zone.broadcastPacket(new SpecialCamera(_overheadDummy.getObjectId(), 930, 160, -20, 0, 1000, 0, 0, 1, 0));
                    startQuestTimer("camera_18b", 0L, _overheadDummy, null, false);
                }
                else if (event.equalsIgnoreCase("camera_18b"))
                {
                    _Zone.broadcastPacket(new SpecialCamera(_overheadDummy.getObjectId(), 600, 180, -25, 0, 10000, 0, 0, 1, 0));
                    _Zone.broadcastPacket(new MagicSkillUse(_scarletDummy, _overheadDummy, 5004, 1, 5800, 0));
                    weakScarlet = ((GrandBoss) addSpawn(SCARLET1, 174232, -88020, -5110, 16384, false, 0L, true));
                    weakScarlet.setIsInvul(true);
                    weakScarlet.setIsImmobilized(true);
                    weakScarlet.disableAllSkills();
                    activeScarlet = weakScarlet;
                    startQuestTimer("camera_19", 2400L, _scarletDummy, null, false);
                    startQuestTimer("camera_19b", 5000L, _scarletDummy, null, false);
                }
                else if (event.equalsIgnoreCase("camera_19"))
                {
                    weakScarlet.teleToLocation(174232, -88020, -5110, 0);
                }
                else if (event.equalsIgnoreCase("camera_19b"))
                {
                    _Zone.broadcastPacket(new SpecialCamera(_scarletDummy.getObjectId(), 800, 180, 10, 1000, 10000, 0, 0, 1, 0));
                    startQuestTimer("camera_20", 2100L, _scarletDummy, null, false);
                }
                else if (event.equalsIgnoreCase("camera_20"))
                {
                    _Zone.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 300, 60, 8, 0, 10000, 0, 0, 1, 0));
                    startQuestTimer("camera_21", 2000L, weakScarlet, null, false);
                }
                else if (event.equalsIgnoreCase("camera_21"))
                {
                    _Zone.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 500, 90, 10, 3000, 5000, 0, 0, 1, 0));
                    startQuestTimer("camera_22", 3000L, weakScarlet, null, false);
                }
                else if (event.equalsIgnoreCase("camera_22"))
                {
                    portrait2 = ((Monster) addSpawn(29049, 175876, -88713, -5000, 28205, false, 0L, false));
                    portrait2.setIsImmobilized(true);
                    portrait2.disableAllSkills();
                    portrait3 = ((Monster) addSpawn(29049, 172608, -88702, -5000, 64817, false, 0L, false));
                    portrait3.setIsImmobilized(true);
                    portrait3.disableAllSkills();
                    portrait1 = ((Monster) addSpawn(29048, 175833, -87165, -5000, 35048, false, 0L, false));
                    portrait1.setIsImmobilized(true);
                    portrait1.disableAllSkills();
                    portrait4 = ((Monster) addSpawn(29048, 172634, -87165, -5000, 57730, false, 0L, false));
                    portrait4.setIsImmobilized(true);
                    portrait4.disableAllSkills();
                    _overheadDummy.deleteMe();
                    _scarletDummy.deleteMe();
                    _overheadDummy = null;
                    _scarletDummy = null;
                    startQuestTimer("camera_23", 2000L, weakScarlet, null, false);
                    startQuestTimer("start_pc", 2000L, weakScarlet, null, false);
                    startQuestTimer("loc_check", 60000L, weakScarlet, null, true);
                    startQuestTimer("songs_play", 10000 + Rnd.get(10000), frintezza, null, false);
                    startQuestTimer("skill01", 10000 + Rnd.get(10000), weakScarlet, null, false);
                }
                else if (event.equalsIgnoreCase("camera_23"))
                {
                    demon1.setIsImmobilized(false);
                    demon2.setIsImmobilized(false);
                    demon3.setIsImmobilized(false);
                    demon4.setIsImmobilized(false);
                    demon1.enableAllSkills();
                    demon2.enableAllSkills();
                    demon3.enableAllSkills();
                    demon4.enableAllSkills();
                    portrait1.setIsImmobilized(false);
                    portrait2.setIsImmobilized(false);
                    portrait3.setIsImmobilized(false);
                    portrait4.setIsImmobilized(false);
                    portrait1.enableAllSkills();
                    portrait2.enableAllSkills();
                    portrait3.enableAllSkills();
                    portrait4.enableAllSkills();
                    weakScarlet.setIsInvul(false);
                    weakScarlet.setIsImmobilized(false);
                    weakScarlet.enableAllSkills();
                    weakScarlet.setRunning();
                    
                    startQuestTimer("spawn_minion", 20000L, portrait1, null, false);
                    startQuestTimer("spawn_minion", 20000L, portrait2, null, false);
                    startQuestTimer("spawn_minion", 20000L, portrait3, null, false);
                    startQuestTimer("spawn_minion", 20000L, portrait4, null, false);
                }
                else if (event.equalsIgnoreCase("stop_pc"))
                {
                    for (Creature cha : _Zone.getCharactersInside())
                    {
                        cha.abortAttack();
                        cha.abortCast();
                        cha.disableAllSkills();
                        cha.setTarget(null);
                        cha.stopMove(null);
                        cha.setIsImmobilized(true);
                        cha.getAI().setIntention(CtrlIntention.IDLE);
                    }
                }
                else if (event.equalsIgnoreCase("stop_npc"))
                {
                    _Heading = npc.getHeading();
                    if (_Heading < 32768)
                        _Angle = Math.abs(180 - (int) (_Heading / 182.044444444D));
                    else
                        _Angle = Math.abs(540 - (int) (_Heading / 182.044444444D));
                }
                else
                {
                    if (event.equalsIgnoreCase("start_pc"))
                    {
                        for (Iterator<Creature> i = _Zone.getCharactersInside().iterator(); i.hasNext();)
                        {
                            Creature cha = i.next();
                            if (cha != frintezza)
                            {
                                cha.enableAllSkills();
                                cha.setIsImmobilized(false);
                            }
                        }
                    }
                    else if (event.equalsIgnoreCase("start_npc"))
                    {
                        npc.setRunning();
                        npc.setIsInvul(false);
                    }
                    else if (event.equalsIgnoreCase("morph_end"))
                    {
                        _OnMorph = 0;
                    }
                    else if (event.equalsIgnoreCase("morph_01"))
                    {
                        _Zone.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 250, _Angle, 12, 2000, 15000, 0, 0, 1, 0));
                        startQuestTimer("morph_02", 3000L, weakScarlet, null, false);
                    }
                    else if (event.equalsIgnoreCase("morph_02"))
                    {
                        _Zone.broadcastPacket(new SocialAction(weakScarlet, 1));
                        weakScarlet.setRHandId(7903);
                        startQuestTimer("morph_03", 4000L, weakScarlet, null, false);
                    }
                    else if (event.equalsIgnoreCase("morph_03"))
                    {
                        startQuestTimer("morph_04", 1500L, weakScarlet, null, false);
                    }
                    else if (event.equalsIgnoreCase("morph_04"))
                    {
                        _Zone.broadcastPacket(new SocialAction(weakScarlet, 4));
                        L2Skill skill = SkillTable.getInstance().getInfo(5017, 1);
                        if (skill != null)
                        {
                            skill.getEffects(weakScarlet, weakScarlet);
                        }
                        startQuestTimer("morph_end", 6000L, weakScarlet, null, false);
                        startQuestTimer("start_pc", 3000L, weakScarlet, null, false);
                        startQuestTimer("start_npc", 3000L, weakScarlet, null, false);
                        startQuestTimer("songs_play", 10000 + Rnd.get(10000), frintezza, null, false);
                        startQuestTimer("skill02", 10000 + Rnd.get(10000), weakScarlet, null, false);
                    }
                    else if (event.equalsIgnoreCase("morph_05a"))
                    {
                        _Zone.broadcastPacket(new SocialAction(frintezza, 4));
                    }
                    else if (event.equalsIgnoreCase("morph_05"))
                    {
                        _Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 250, 120, 15, 0, 1000, 0, 0, 1, 0));
                        startQuestTimer("morph_06", 0L, frintezza, null, false);
                    }
                    else if (event.equalsIgnoreCase("morph_06"))
                    {
                        _Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 250, 120, 15, 0, 10000, 0, 0, 1, 0));
                        
                        cancelQuestTimers("loc_check");
                        
                        _Scarlet_x = weakScarlet.getX();
                        _Scarlet_y = weakScarlet.getY();
                        _Scarlet_z = weakScarlet.getZ();
                        _Scarlet_h = weakScarlet.getHeading();
                        weakScarlet.deleteMe();
                        weakScarlet = null;
                        activeScarlet = null;
                        weakScarlet = ((GrandBoss) addSpawn(SCARLET1, _Scarlet_x, _Scarlet_y, _Scarlet_z, _Scarlet_h, false, 0L, false));
                        weakScarlet.setIsInvul(true);
                        weakScarlet.setIsImmobilized(true);
                        weakScarlet.disableAllSkills();
                        weakScarlet.setRHandId(7903);
                        
                        startQuestTimer("morph_07", 7000L, frintezza, null, false);
                    }
                    else if (event.equalsIgnoreCase("morph_07"))
                    {
                        _Zone.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5006, 1, 34000, 0));
                        _Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 500, 70, 15, 3000, 10000, 0, 0, 1, 0));
                        startQuestTimer("morph_08", 3000L, frintezza, null, false);
                    }
                    else if (event.equalsIgnoreCase("morph_08"))
                    {
                        _Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 2500, 90, 12, 6000, 10000, 0, 0, 1, 0));
                        startQuestTimer("morph_09", 3000L, frintezza, null, false);
                    }
                    else if (event.equalsIgnoreCase("morph_09"))
                    {
                        _Zone.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 250, _Angle, 12, 0, 1000, 0, 0, 1, 0));
                        startQuestTimer("morph_10", 0L, weakScarlet, null, false);
                    }
                    else if (event.equalsIgnoreCase("morph_10"))
                    {
                        _Zone.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 250, _Angle, 12, 0, 10000, 0, 0, 1, 0));
                        startQuestTimer("morph_11", 500L, weakScarlet, null, false);
                    }
                    else if (event.equalsIgnoreCase("morph_11"))
                    {
                        weakScarlet.doDie(weakScarlet);
                        _Zone.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 450, _Angle, 14, 8000, 8000, 0, 0, 1, 0));
                        
                        startQuestTimer("morph_12", 6250L, weakScarlet, null, false);
                        startQuestTimer("morph_13", 7200L, weakScarlet, null, false);
                    }
                    else if (event.equalsIgnoreCase("morph_12"))
                    {
                        weakScarlet.deleteMe();
                        weakScarlet = null;
                    }
                    else if (event.equalsIgnoreCase("morph_13"))
                    {
                        strongScarlet = ((GrandBoss) addSpawn(SCARLET2, _Scarlet_x, _Scarlet_y, _Scarlet_z, _Scarlet_h, false, 0L, false));
                        strongScarlet.setIsInvul(true);
                        strongScarlet.setIsImmobilized(true);
                        strongScarlet.disableAllSkills();
                        activeScarlet = strongScarlet;
                        
                        _Zone.broadcastPacket(new SpecialCamera(strongScarlet.getObjectId(), 450, _Angle, 12, 500, 14000, 0, 0, 1, 0));
                        
                        startQuestTimer("morph_14", 3000L, strongScarlet, null, false);
                        startQuestTimer("loc_check", 60000L, strongScarlet, null, true);
                    }
                    else if (event.equalsIgnoreCase("morph_14"))
                    {
                        startQuestTimer("morph_15", 5100L, strongScarlet, null, false);
                    }
                    else if (event.equalsIgnoreCase("morph_15"))
                    {
                        _Zone.broadcastPacket(new SocialAction(strongScarlet, 2));
                        L2Skill skill = SkillTable.getInstance().getInfo(5017, 1);
                        if (skill != null)
                        {
                            skill.getEffects(strongScarlet, strongScarlet);
                        }
                        startQuestTimer("morph_end", 9000L, strongScarlet, null, false);
                        startQuestTimer("start_pc", 6000L, strongScarlet, null, false);
                        startQuestTimer("start_npc", 6000L, strongScarlet, null, false);
                        startQuestTimer("songs_play", 10000 + Rnd.get(10000), frintezza, null, false);
                        startQuestTimer("skill03", 10000 + Rnd.get(10000), strongScarlet, null, false);
                    }
                    else if (event.equalsIgnoreCase("morph_16"))
                    {
                        _Zone.broadcastPacket(new SpecialCamera(strongScarlet.getObjectId(), 300, _Angle - 180, 5, 0, 7000, 0, 0, 1, 0));
                        startQuestTimer("morph_17", 0L, strongScarlet, null, false);
                    }
                    else if (event.equalsIgnoreCase("morph_17"))
                    {
                        _Zone.broadcastPacket(new SpecialCamera(strongScarlet.getObjectId(), 200, _Angle, 85, 4000, 10000, 0, 0, 1, 0));
                        startQuestTimer("morph_17b", 7400L, frintezza, null, false);
                        startQuestTimer("morph_18", 7500L, frintezza, null, false);
                    }
                    else if (event.equalsIgnoreCase("morph_17b"))
                    {
                        frintezza.doDie(frintezza);
                    }
                    else if (event.equalsIgnoreCase("morph_18"))
                    {
                        _Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 100, 120, 5, 0, 7000, 0, 0, 1, 0));
                        startQuestTimer("morph_19", 0L, frintezza, null, false);
                    }
                    else if (event.equalsIgnoreCase("morph_19"))
                    {
                        _Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 100, 90, 5, 5000, 15000, 0, 0, 1, 0));
                        startQuestTimer("morph_20", 7000L, frintezza, null, false);
                        startQuestTimer("spawn_cubes", 7000L, frintezza, null, false);
                    }
                    else if (event.equalsIgnoreCase("morph_20"))
                    {
                        _Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 900, 90, 25, 7000, 10000, 0, 0, 1, 0));
                        startQuestTimer("start_pc", 7000L, frintezza, null, false);
                    }
                    else if (event.equalsIgnoreCase("songs_play"))
                    {
                        if ((frintezza != null) && (!frintezza.isDead()) && (_OnMorph == 0))
                        {
                            _OnSong = Rnd.get(1, 5);
                            if ((_OnSong == 1) && (_ThirdMorph == 1) && (strongScarlet.getCurrentHp() < strongScarlet.getMaxHp() * 0.6D) && (Rnd.get(100) < 80))
                            {
                                _Zone.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5007, 1, 32000, 0));
                                startQuestTimer("songs_effect", 5000L, frintezza, null, false);
                                startQuestTimer("songs_play", 32000 + Rnd.get(10000), frintezza, null, false);
                            }
                            else if ((_OnSong == 2) || (_OnSong == 3))
                            {
                                _Zone.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5007, _OnSong, 32000, 0));
                                startQuestTimer("songs_effect", 5000L, frintezza, null, false);
                                startQuestTimer("songs_play", 32000 + Rnd.get(10000), frintezza, null, false);
                            }
                            else if ((_OnSong == 4) && (_SecondMorph == 1))
                            {
                                _Zone.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5007, 4, 31000, 0));
                                startQuestTimer("songs_effect", 5000L, frintezza, null, false);
                                startQuestTimer("songs_play", 31000 + Rnd.get(10000), frintezza, null, false);
                            }
                            else if ((_OnSong == 5) && (_ThirdMorph == 1) && (_Abnormal == 0))
                            {
                                _Abnormal = 1;
                                _Zone.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5007, 5, 35000, 0));
                                startQuestTimer("songs_effect", 5000L, frintezza, null, false);
                                startQuestTimer("songs_play", 35000 + Rnd.get(10000), frintezza, null, false);
                            }
                            else
                            {
                                startQuestTimer("songs_play", 5000 + Rnd.get(5000), frintezza, null, false);
                            }
                        }
                    }
                    else
                    {
                        L2Skill skill;
                        if (event.equalsIgnoreCase("songs_effect"))
                        {
                            skill = SkillTable.getInstance().getInfo(5008, _OnSong);
                            
                            if (skill == null)
                                return null;
                            
                            if ((_OnSong == 1) || (_OnSong == 2) || (_OnSong == 3))
                            {
                                if ((frintezza != null) && (!frintezza.isDead()) && (activeScarlet != null) && (!activeScarlet.isDead()))
                                    skill.getEffects(frintezza, activeScarlet);
                            }
                            else if (_OnSong == 4)
                            {
                                for (Creature cha : _Zone.getCharactersInside())
                                {
                                    if (((cha instanceof Player)) && (Rnd.get(100) < 0))
                                        skill.getEffects(frintezza, cha);
                                    cha.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(5008, 4));
                                }
                            }
                            else if (_OnSong == 5)
                            {
                                for (Creature cha : _Zone.getCharactersInside())
                                {
                                    if (((cha instanceof Player)) && (Rnd.get(100) < 0))
                                    {
                                        cha.abortAttack();
                                        cha.abortCast();
                                        cha.disableAllSkills();
                                        cha.stopMove(null);
                                        cha.setIsParalyzed(true);
                                        cha.setIsImmobilized(true);
                                        cha.getAI().setIntention(CtrlIntention.IDLE);
                                        skill.getEffects(frintezza, cha);
                                        cha.startAbnormalEffect(AbnormalEffect.DANCE_STUNNED);
                                        cha.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(5008, 5));
                                    }
                                }
                                startQuestTimer("stop_effect", 25000L, frintezza, null, false);
                            }
                        }
                        else if (event.equalsIgnoreCase("stop_effect"))
                        {
                            for (Creature cha : _Zone.getCharactersInside())
                            {
                                if ((cha instanceof Player))
                                {
                                    cha.stopAbnormalEffect(AbnormalEffect.DANCE_STUNNED);
                                    cha.stopAbnormalEffect(AbnormalEffect.FLOATING_ROOT);
                                    cha.enableAllSkills();
                                    cha.setIsImmobilized(false);
                                    cha.setIsParalyzed(false);
                                }
                            }
                            _Abnormal = 0;
                        }
                        else if (event.equalsIgnoreCase("attack_stop"))
                        {
                            cancelQuestTimers("skill01");
                            cancelQuestTimers("skill02");
                            cancelQuestTimers("skill03");
                            cancelQuestTimers("songs_play");
                            cancelQuestTimers("songs_effect");
                            
                            _Zone.broadcastPacket(new MagicSkillCanceld(frintezza.getObjectId()));
                        }
                        else if (event.equalsIgnoreCase("check_hp"))
                        {
                            if (npc.isDead())
                            {
                                _OnMorph = 1;
                                _Zone.broadcastPacket(new PlaySound(1, "BS01_D", npc));
                                
                                startQuestTimer("attack_stop", 0L, frintezza, null, false);
                                startQuestTimer("stop_pc", 0L, npc, null, false);
                                startQuestTimer("stop_npc", 0L, npc, null, false);
                                startQuestTimer("morph_16", 0L, npc, null, false);
                            }
                            else
                            {
                                _CheckDie += 10;
                                if (_CheckDie < 3000)
                                    startQuestTimer("check_hp", 10L, npc, null, false);
                                else
                                {
                                    _OnCheck = 0;
                                    _CheckDie = 0;
                                }
                            }
                        }
                        else if (event.equalsIgnoreCase("skill01"))
                        {
                            if ((weakScarlet != null) && (!weakScarlet.isDead()) && (_SecondMorph == 0) && (_ThirdMorph == 0) && (_OnMorph == 0))
                            {
                                int i = Rnd.get(0, 1);
                                L2Skill skill1 = SkillTable.getInstance().getInfo(_skill[i][0], _skill[i][1]);
                                if (skill1 != null)
                                {
                                    weakScarlet.stopMove(null);
                                    weakScarlet.setIsCastingNow(true);
                                    weakScarlet.doCast(skill1);
                                }
                                startQuestTimer("skill01", _skill[i][2] + 5000 + Rnd.get(10000), npc, null, false);
                            }
                        }
                        else if (event.equalsIgnoreCase("skill02"))
                        {
                            if ((weakScarlet != null) && (!weakScarlet.isDead()) && (_SecondMorph == 1) && (_ThirdMorph == 0) && (_OnMorph == 0))
                            {
                                int i = 0;
                                
                                if (_Abnormal == 0)
                                    i = Rnd.get(2, 5);
                                else
                                    i = Rnd.get(2, 4);
                                
                                L2Skill skill2 = SkillTable.getInstance().getInfo(_skill[i][0], _skill[i][1]);
                                if (skill2 != null)
                                {
                                    weakScarlet.stopMove(null);
                                    weakScarlet.setIsCastingNow(true);
                                    weakScarlet.doCast(skill2);
                                }
                                startQuestTimer("skill02", _skill[i][2] + 5000 + Rnd.get(10000), npc, null, false);
                                
                                if (i == 5)
                                    _Abnormal = 1;
                                startQuestTimer("float_effect", 4000L, weakScarlet, null, false);
                                
                            }
                        }
                        else
                        {
                            if (event.equalsIgnoreCase("skill03"))
                            {
                                if ((strongScarlet != null) && (!strongScarlet.isDead()) && (_SecondMorph == 1) && (_ThirdMorph == 1) && (_OnMorph == 0))
                                {
                                    int i = 0;
                                    if (_Abnormal == 0)
                                    {
                                        i = Rnd.get(6, 10);
                                    }
                                    else
                                    {
                                        i = Rnd.get(6, 9);
                                    }
                                    L2Skill skill3 = SkillTable.getInstance().getInfo(_skill[i][0], _skill[i][1]);
                                    if (skill3 != null)
                                    {
                                        strongScarlet.stopMove(null);
                                        strongScarlet.setIsCastingNow(true);
                                        strongScarlet.doCast(skill3);
                                    }
                                    startQuestTimer("skill03", _skill[i][2] + 5000 + Rnd.get(10000), npc, null, false);
                                    if (i == 10)
                                    {
                                        _Abnormal = 1;
                                        startQuestTimer("float_effect", 3000L, npc, null, false);
                                    }
                                }
                            }
                            else if (event.equalsIgnoreCase("float_effect"))
                            {
                                if (npc.isCastingNow())
                                {
                                    startQuestTimer("float_effect", 500L, npc, null, false);
                                }
                                else
                                {
                                    for (Creature cha : _Zone.getCharactersInside())
                                    {
                                        if ((cha instanceof Player))
                                        {
                                            if (cha.getFirstEffect(5016) != null)
                                            {
                                                cha.abortAttack();
                                                cha.abortCast();
                                                cha.disableAllSkills();
                                                cha.stopMove(null);
                                                cha.setIsParalyzed(true);
                                                cha.setIsImmobilized(true);
                                                cha.getAI().setIntention(CtrlIntention.IDLE);
                                                cha.startAbnormalEffect(AbnormalEffect.FLOATING_ROOT);
                                            }
                                        }
                                    }
                                    startQuestTimer("stop_effect", 25000L, npc, null, false);
                                }
                            }
                            else if (event.equalsIgnoreCase("action"))
                            {
                                _Zone.broadcastPacket(new SocialAction(npc, 1));
                            }
                            else if (event.equalsIgnoreCase("bomber"))
                            {
                                _Bomber = 0;
                            }
                            else if (event.equalsIgnoreCase("room_final"))
                            {
                                _Zone.broadcastPacket(new NpcSay(npc.getObjectId(), 1, npc.getNpcId(), "Exceeded his time limit, challenge failed!"));
                               // _Zone.oustAllPlayers();
                                
                                cancelQuestTimers("waiting");
                                cancelQuestTimers("frintezza_despawn");
                                startQuestTimer("clean", 1000L, npc, null, false);
                                startQuestTimer("close", 1000L, npc, null, false);
                                startQuestTimer("room1_del", 1000L, npc, null, false);
                                startQuestTimer("room2_del", 1000L, npc, null, false);
                                
                                GrandBossManager.getInstance().setBossStatus(FRINTEZZA, 0);
                            }
                            else if (event.equalsIgnoreCase("frintezza_despawn"))
                            {
                                temp = System.currentTimeMillis() - _LastAction;
                                if (temp > 900000L)
                                {
                                  //  _Zone.oustAllPlayers();
                                    
                                    cancelQuestTimers("waiting");
                                    cancelQuestTimers("loc_check");
                                    cancelQuestTimers("room_final");
                                    cancelQuestTimers("spawn_minion");
                                    startQuestTimer("clean", 1000L, npc, null, false);
                                    startQuestTimer("close", 1000L, npc, null, false);
                                    startQuestTimer("attack_stop", 1000L, npc, null, false);
                                    startQuestTimer("room1_del", 1000L, npc, null, false);
                                    startQuestTimer("room2_del", 1000L, npc, null, false);
                                    startQuestTimer("room3_del", 1000L, npc, null, false);
                                    startQuestTimer("minions_despawn", 1000L, npc, null, false);
                                    
                                    GrandBossManager.getInstance().setBossStatus(FRINTEZZA, 0);
                                    
                                    cancelQuestTimers("frintezza_despawn");
                                }
                            }
                            else if (event.equalsIgnoreCase("minions_despawn"))
                            {
                                for (int i = 0; i < Minions.size(); i++)
                                {
                                    Attackable mob = Minions.get(i);
                                    if (mob != null)
                                    {
                                        mob.decayMe();
                                    }
                                }
                                Minions.clear();
                            }
                            else if (event.equalsIgnoreCase("spawn_minion"))
                            {
                                if ((npc != null) && (!npc.isDead()) && (frintezza != null) && (!frintezza.isDead()))
                                {
                                    Npc mob = addSpawn(npc.getNpcId() + 2, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 0L, false);
                                    ((Attackable) mob).setIsRaidMinion(true);
                                    Minions.add((Attackable) mob);
                                    startQuestTimer("action", 200L, mob, null, false);
                                    startQuestTimer("spawn_minion", 18000L, npc, null, false);
                                }
                            }
                            else if (event.equalsIgnoreCase("spawn_cubes"))
                            {
                                addSpawn(CUBE, 174232, -88020, -5110, 16384, false, 900000L, false);
                            }
                            else if (event.equalsIgnoreCase("frintezza_unlock"))
                            {
                                GrandBossManager.getInstance().setBossStatus(FRINTEZZA, 0);
                            }
                          //  else if (event.equalsIgnoreCase("remove_players"))
                          //  {
                          //      _Zone.oustAllPlayers();
                          //  }
                        }
                    }
                }
            }
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    @Override
    public String onTalk(Npc npc, Player player)
    {
        if (npc.getNpcId() == CUBE)
        {
            int x = 150037 + Rnd.get(500);
            int y = -57720 + Rnd.get(500);
            player.teleToLocation(x, y, -2976, 0);
            return null;
        }
        
        String htmltext = "";
        if (GrandBossManager.getInstance().getBossStatus(FRINTEZZA) == DEAD)
        {
            htmltext = "<html><body>There is nothing beyond the Magic Force Field. Come back later.<br>(You may not enter because Frintezza is not inside the Imperial Tomb.)</body></html>";
        }
        else if (GrandBossManager.getInstance().getBossStatus(FRINTEZZA) == DORMANT || GrandBossManager.getInstance().getBossStatus(FRINTEZZA) == WAITING)
        {
            boolean party_check_success = true;
            
            if(Config.BYPASS_FRINTEZZA_PARTIES_CHECK)
            {
                
                if ((!player.isInParty() || !player.getParty().isLeader(player))
                        || (player.getParty().getCommandChannel() == null)
                        || (player.getParty().getCommandChannel().getChannelLeader() != player))
                {
                    htmltext = "<html><body>No reaction. Contact must be initiated by the Command Channel Leader.</body></html>";
                    party_check_success = false;
                }
                else if (player.getParty().getCommandChannel().getPartys().size() < Config.FRINTEZZA_MIN_PARTIES || player.getParty().getCommandChannel().getPartys().size() > Config.FRINTEZZA_MAX_PARTIES)
                {
                    htmltext = "<html><body>Your command channel needs to have at least "+Config.FRINTEZZA_MIN_PARTIES+" parties and a maximum of "+Config.FRINTEZZA_MAX_PARTIES+".</body></html>";
                    party_check_success = false;
                }
                
            }
            
            if(party_check_success){
            
                if (player.getInventory().getItemByItemId(Config.QUEST_FRINTEZZA) == null)
                {
                    htmltext = "<html><body>You dont have required item.</body></html>";
                }
                else
                {
                    player.destroyItemByItemId("Quest", Config.QUEST_FRINTEZZA, 1, player, true);
                    if(GrandBossManager.getInstance().getBossStatus(FRINTEZZA) == DORMANT){
                    	
                        startQuestTimer("close", 0, npc, null, false);
                        startQuestTimer("room1_spawn", 5000, npc, null, false);
                        startQuestTimer("room_final", 2100000, null, null, false);
                        startQuestTimer("frintezza_despawn", 60000, null, null, true);
                        
                    GrandBossManager.getInstance().setBossStatus(FRINTEZZA, WAITING);
                    
                    }
                    
                    _LastAction = System.currentTimeMillis();
                    
                    if(!Config.BYPASS_FRINTEZZA_PARTIES_CHECK){
                        
                        if(player.getParty()!=null){
                            
                            L2CommandChannel CC = player.getParty().getCommandChannel();
                            
                            if(CC != null){ //teleport all parties into CC
                                
                                for (L2Party party : CC.getPartys())
                                {
                                    if (party == null)
                                        continue;
                                    
                                    synchronized(_PlayersInside){
                                        
                                        for (Player member : party.getPartyMembers())
                                        {
                                            if (member == null || member.getLevel() < 74)
                                                continue;
                                            if (!member.isInsideRadius(npc, 700, false, false))
                                                continue;
                                            if (_PlayersInside.size() > 45)
                                            {
                                                member.sendMessage("The number of challenges have been full, so can not enter.");
                                                break;
                                            }
                                            _PlayersInside.add(member);
                                            _Zone.allowPlayerEntry(member, 300);
                                            member.teleToLocation(_invadeLoc[_LocCycle][0] + Rnd.get(50), _invadeLoc[_LocCycle][1] + Rnd.get(50), _invadeLoc[_LocCycle][2],0);
                                        }
                                        if (_PlayersInside.size() > 45)
                                            break;
                                        
                                    }
                                    
                                    _LocCycle++;
                                    if (_LocCycle >= 6)
                                        _LocCycle = 1;
                                }
                                
                            }else{ //teleport just actual party
                                
                                L2Party party = player.getParty();
                                
                                for (Player member : party.getPartyMembers())
                                {
                                    if (member == null || member.getLevel() < 74)
                                        continue;
                                    if (!member.isInsideRadius(npc, 700, false, false))
                                        continue;
                                    
                                    synchronized(_PlayersInside){
                                        if (_PlayersInside.size() > 45)
                                        {
                                            member.sendMessage("The number of challenges have been full, so can not enter.");
                                            break;
                                        }
                                        _PlayersInside.add(member);
                                    }
                                    
                                    _Zone.allowPlayerEntry(member, 300);
                                    member.teleToLocation(_invadeLoc[_LocCycle][0] + Rnd.get(50), _invadeLoc[_LocCycle][1] + Rnd.get(50), _invadeLoc[_LocCycle][2],0);
                                }
                                
                                _LocCycle++;
                                if (_LocCycle >= 6)
                                    _LocCycle = 1;
                                
                            }
                            
                        }else{ //teleport just player
                            
                            if (player.isInsideRadius(npc, 700, false, false)){
                                
                                synchronized(_PlayersInside){
                                    _PlayersInside.add(player);
                                    
                                }
                                _Zone.allowPlayerEntry(player, 300);
                                player.teleToLocation(_invadeLoc[_LocCycle][0] + Rnd.get(50), _invadeLoc[_LocCycle][1] + Rnd.get(50), _invadeLoc[_LocCycle][2],0);
                                
                            }
                            
                        }
                        
                    }else{
                        
                        L2CommandChannel CC = player.getParty().getCommandChannel();
                        
                        for (L2Party party : CC.getPartys())
                        {
                            if (party == null)
                                continue;
                            
                            synchronized(_PlayersInside){
                                for (Player member : party.getPartyMembers())
                                {
                                    if (member == null || member.getLevel() < 74)
                                        continue;
                                    if (!member.isInsideRadius(npc, 700, false, false))
                                        continue;
                                    if (_PlayersInside.size() > 45)
                                    {
                                        member.sendMessage("The number of challenges have been full, so can not enter.");
                                        break;
                                    }
                                    _PlayersInside.add(member);
                                    _Zone.allowPlayerEntry(member, 300);
                                    member.teleToLocation(_invadeLoc[_LocCycle][0] + Rnd.get(50), _invadeLoc[_LocCycle][1] + Rnd.get(50), _invadeLoc[_LocCycle][2],0);
                                }
                                if (_PlayersInside.size() > 45)
                                    break;
                                
                            }
                            
                            _LocCycle++;
                            if (_LocCycle >= 6)
                                _LocCycle = 1;
                        }
                        
                    }
                    
                    
                }
                
            }
            for (int i = 25150051; i <= 25150058; i++)
                DoorTable.getInstance().getDoor(i).openMe();
            
        }
        else
            htmltext = "<html><body>Someone else is already inside the Magic Force Field. Try again later.</body></html>";
        
        return htmltext;
    }
    
    @Override
    public String onAttack(Npc npc, Player attacker, int damage, boolean isPet, L2Skill skill)
    {
        _LastAction = System.currentTimeMillis();
        if (npc.getNpcId() == FRINTEZZA)
        {
            npc.setCurrentHpMp(npc.getMaxHp(), 0.0D);
            return null;
        }
        if ((npc.getNpcId() == SCARLET1) && (_SecondMorph == 0) && (_ThirdMorph == 0) && (_OnMorph == 0) && (npc.getCurrentHp() < npc.getMaxHp() * 0.75D) && (GrandBossManager.getInstance().getBossStatus(FRINTEZZA) == FIGHTING))
        {
            startQuestTimer("attack_stop", 0L, frintezza, null, false);
            
            _SecondMorph = 1;
            _OnMorph = 1;
            
            startQuestTimer("stop_pc", 1000L, npc, null, false);
            startQuestTimer("stop_npc", 1000L, npc, null, false);
            startQuestTimer("morph_01", 1100L, npc, null, false);
        }
        else if ((npc.getNpcId() == SCARLET1) && (_SecondMorph == 1) && (_ThirdMorph == 0) && (_OnMorph == 0) && (npc.getCurrentHp() < npc.getMaxHp() * 0.5D) && (GrandBossManager.getInstance().getBossStatus(FRINTEZZA) == FIGHTING))
        {
            startQuestTimer("attack_stop", 0L, frintezza, null, false);
            
            _ThirdMorph = 1;
            _OnMorph = 1;
            
            startQuestTimer("stop_pc", 2000L, npc, null, false);
            startQuestTimer("stop_npc", 2000L, npc, null, false);
            startQuestTimer("morph_05a", 2000L, npc, null, false);
            startQuestTimer("morph_05", 2100L, npc, null, false);
        }
        else if ((npc.getNpcId() == SCARLET2) && (_SecondMorph == 1) && (_ThirdMorph == 1) && (_OnCheck == 0) && (damage >= npc.getCurrentHp()) && (GrandBossManager.getInstance().getBossStatus(FRINTEZZA) == FIGHTING))
        {
            _OnCheck = 1;
            startQuestTimer("check_hp", 0L, npc, null, false);
        }
        else if (((npc.getNpcId() == 29050) || (npc.getNpcId() == 29051)) && (_Bomber == 0))
        {
            if (npc.getCurrentHp() < npc.getMaxHp() * 0.1D)
            {
                if (Rnd.get(100) < 30)
                {
                    _Bomber = 1;
                    startQuestTimer("bomber", 3000L, npc, null, false);
                    
                    L2Skill sk = SkillTable.getInstance().getInfo(5011, 1);
                    if (sk != null)
                    {
                        npc.doCast(sk);
                    }
                }
            }
        }
        return super.onAttack(npc, attacker, damage, isPet, skill);
    }
    
    @Override
    public String onKill(Npc npc, Player killer, boolean isPet)
    {
        if (npc.getNpcId() == SCARLET2)
        {
            _Zone.broadcastPacket(new PlaySound(1, "BS01_D", npc));
            
            startQuestTimer("stop_pc", 0L, null, null, false);
            startQuestTimer("stop_npc", 0L, npc, null, false);
            startQuestTimer("morph_16", 0L, npc, null, false);
            
            GrandBossManager.getInstance().setBossStatus(FRINTEZZA, 3);
            
    		long respawnTime;
            if(Config.FRINTEZZA_CUSTOM_SPAWN_ENABLED && Config.FindNext(Config.FRINTEZZA_CUSTOM_SPAWN_TIMES) != null)
            {
				respawnTime = Config.FindNext(Config.FRINTEZZA_CUSTOM_SPAWN_TIMES).getTimeInMillis() - System.currentTimeMillis();
			}
            else
            {
                respawnTime = Config.SPAWN_INTERVAL_FRINTEZZA + Rnd.get(-Config.RANDOM_SPAWN_TIME_FRINTEZZA, Config.RANDOM_SPAWN_TIME_FRINTEZZA);
                respawnTime *= 3600000L;
            }
            
            cancelQuestTimers("spawn_minion");
            cancelQuestTimers("frintezza_despawn");
            startQuestTimer("close", 0L, null, null, false);
            startQuestTimer("rooms_del", 0L, npc, null, false);
            startQuestTimer("minions_despawn", 0L, null, null, false);
           // startQuestTimer("remove_players", 900000L, null, null, false);
            startQuestTimer("frintezza_unlock", respawnTime, null, null, false);
            
            StatsSet info = GrandBossManager.getInstance().getStatsSet(FRINTEZZA);
            info.set("respawn_time", System.currentTimeMillis() + respawnTime);
            GrandBossManager.getInstance().setStatsSet(FRINTEZZA, info);
        }
        else if (npc.getNpcId() == 18328)
        {
            _KillHallAlarmDevice += 1;
            if (_KillHallAlarmDevice == 3)
            {
                for (int i = 25150051; i <= 25150058; i++)
                {
                    DoorTable.getInstance().getDoor(i).openMe();
                }
            }
            else if (_KillHallAlarmDevice == 4)
            {
                startQuestTimer("room1_del", 100L, npc, null, false);
                startQuestTimer("room2_spawn", 100L, npc, null, false);
                DoorTable.getInstance().getDoor(25150042).openMe();
                DoorTable.getInstance().getDoor(25150043).openMe();
            }
        }
        else if (npc.getNpcId() == 18339)
        {
            _KillDarkChoirPlayer += 1;
            if (_KillDarkChoirPlayer == 2)
            {
               // DoorTable.getInstance().getDoor(25150042).closeMe();
                //DoorTable.getInstance().getDoor(25150043).closeMe();
                for (int i = 25150061; i <= 25150070; i++)
                {
                    DoorTable.getInstance().getDoor(i).openMe();
                }
                startQuestTimer("room2_spawn2", 1000L, npc, null, false);
            }
        }
        else if (npc.getNpcId() == 18334)
        {
            _KillDarkChoirCaptain += 1;
            if (_KillDarkChoirCaptain == 8)
            {
                startQuestTimer("room2_del", 100L, npc, null, false);
                
                DoorTable.getInstance().getDoor(25150045).openMe();
                DoorTable.getInstance().getDoor(25150046).openMe();
                
                startQuestTimer("waiting", Config.WAIT_TIME_FRINTEZZA, npc, null, false);
                waiter(Config.WAIT_TIME_FRINTEZZA);
                if (GrandBossManager.getInstance().getBossStatus(FRINTEZZA) == WAITING)
        		{
                	GrandBossManager._announce = true;
        			ThreadPool.schedule(new Runnable()
        			{
        				@Override
        				public void run()
        				{
        					waiter(Config.WAIT_TIME_FRINTEZZA);
        				}
        			}, 1);
        		}
                cancelQuestTimers("room_final");
            }
        }
        return super.onKill(npc, killer, isPet);
    }
    
	public static void waiter(long interval)
	{
		long startWaiterTime = System.currentTimeMillis();
		int seconds = (int) (interval / 1000);
		
		while (startWaiterTime + interval > System.currentTimeMillis() && GrandBossManager._announce)
		{
			seconds--; // Here because we don't want to see two time announce at the same time
			
			switch (seconds)
			{
				case 3600: // 1 hour left
					GrandBossManager.AnnounceGrandBoss("Spawn Frintezza in " + seconds / 60 / 60 + " hour(s)!");
					break;
				case 1799: // 10 minutes left
					GrandBossManager.AnnounceGrandBoss("Spawn Frintezza in 30 minute(s) !");
					break;
				case 599: // 10 minutes left
					GrandBossManager.AnnounceGrandBoss("Spawn Frintezza in 10 minute(s) !");
					break;
				case 299: // 10 minutes left
					GrandBossManager.AnnounceGrandBoss("Spawn Frintezza in 5 minute(s) !");
					break;
				
				case 1500: // 25 minutes left
				case 1200: // 20 minutes left
				case 900: // 15 minutes left
				case 540: // 9 minutes left
				case 480: // 8 minutes left
				case 420: // 7 minutes left
				case 360: // 6 minutes left
				case 240: // 4 minutes left
				case 180: // 3 minutes left
				case 120: // 2 minutes left
				case 60: // 1 minute left
					GrandBossManager.AnnounceGrandBoss("Spawn Frintezza in " + seconds / 60 + " minute(s) !");
					break;
				case 30: // 30 seconds left
				case 15: // 15 seconds left
					GrandBossManager.AnnounceGrandBoss("Spawn Frintezza in " + seconds + " second(s) !");
					break;
				
				case 6: // 3 seconds left
				case 5: // 3 seconds left
				case 4: // 3 seconds left
				case 3: // 2 seconds left
				case 2: // 1 seconds left
					GrandBossManager.AnnounceGrandBoss("Spawn Frintezza in " + (seconds - 1) + " second(s) !");
					break;
				
				case 1: // 1 seconds left
				{
					if (GrandBossManager._announce)
						GrandBossManager.AnnounceGrandBoss("Frintezza Is alive, Door to boss closed !");
					GrandBossManager._announce = false;
				}
					break;
			}
			
			long startOneSecondWaiterStartTime = System.currentTimeMillis();
			
			// Only the try catch with Thread.sleep(1000) give bad countdown on high wait times
			while (startOneSecondWaiterStartTime + 1000 > System.currentTimeMillis())
			{
				try
				{
					Thread.sleep(1);
				}
				catch (InterruptedException ie)
				{
				}
			}
		}
	}
}