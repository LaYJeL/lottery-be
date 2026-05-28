import { CheckCircle2, Circle, Star, Gift, Calendar, TrendingUp } from 'lucide-react';

interface TasksProps {
  user: any;
}

export function Tasks({ user }: TasksProps) {
  const dailyTasks = [
    {
      id: 1,
      title: 'Daily Login',
      description: 'Sign in to your account',
      reward: 10,
      completed: true,
    },
    {
      id: 2,
      title: 'Check Lottery Results',
      description: 'View today\'s lottery results',
      reward: 5,
      completed: true,
    },
    {
      id: 3,
      title: 'Share on Social Media',
      description: 'Share LuckyPlay with your friends',
      reward: 15,
      completed: false,
    },
    {
      id: 4,
      title: 'Vote in Competition',
      description: 'Vote for your favorite entry',
      reward: 10,
      completed: false,
    },
  ];

  const weeklyTasks = [
    {
      id: 5,
      title: 'Purchase 5 Lottery Tickets',
      description: 'Buy tickets for any lottery',
      reward: 50,
      progress: 3,
      total: 5,
    },
    {
      id: 6,
      title: 'Complete 10 Daily Tasks',
      description: 'Finish all daily tasks for a week',
      reward: 100,
      progress: 6,
      total: 10,
    },
    {
      id: 7,
      title: 'Enter 3 Competitions',
      description: 'Submit entries to competitions',
      reward: 75,
      progress: 1,
      total: 3,
    },
  ];

  const specialTasks = [
    {
      id: 8,
      title: 'Verify Your Identity',
      description: 'Complete KYC verification',
      reward: 200,
      icon: '🎯',
    },
    {
      id: 9,
      title: 'Invite 5 Friends',
      description: 'Refer friends to join LuckyPlay',
      reward: 500,
      icon: '👥',
    },
    {
      id: 10,
      title: 'Win Your First Prize',
      description: 'Win any lottery or competition',
      reward: 1000,
      icon: '🏆',
    },
  ];

  const achievements = [
    { id: 1, name: 'Early Bird', icon: '🌅', unlocked: true },
    { id: 2, name: 'Lucky Seven', icon: '🍀', unlocked: true },
    { id: 3, name: 'Social Butterfly', icon: '🦋', unlocked: false },
    { id: 4, name: 'Big Winner', icon: '💎', unlocked: false },
    { id: 5, name: 'Consistent Player', icon: '⭐', unlocked: true },
    { id: 6, name: 'Competition King', icon: '👑', unlocked: false },
  ];

  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl mb-2">Tasks & Rewards</h1>
          <p className="text-gray-600">Complete tasks to earn reputation points</p>
        </div>
        <div className="text-right">
          <div className="text-sm text-gray-600">Reputation Score</div>
          <div className="text-2xl text-yellow-600 flex items-center justify-end space-x-2">
            <Star className="w-6 h-6 fill-yellow-600" />
            <span>{user?.reputation}</span>
          </div>
        </div>
      </div>

      {/* Reputation Progress */}
      <div className="bg-gradient-to-r from-yellow-500 to-orange-500 p-6 rounded-2xl text-white">
        <div className="flex items-center justify-between mb-4">
          <div>
            <div className="text-sm text-yellow-100 mb-1">Current Level</div>
            <div className="text-2xl">Gold Member</div>
          </div>
          <div className="text-right">
            <div className="text-sm text-yellow-100 mb-1">Next Level</div>
            <div className="text-2xl">Platinum (150 points away)</div>
          </div>
        </div>
        <div className="h-3 bg-white/20 rounded-full overflow-hidden">
          <div
            className="h-full bg-white rounded-full"
            style={{ width: '75%' }}
          />
        </div>
        <div className="mt-4 flex items-center space-x-2">
          <TrendingUp className="w-5 h-5" />
          <span className="text-sm">+85 points this week</span>
        </div>
      </div>

      {/* Daily Tasks */}
      <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl">Daily Tasks</h2>
          <div className="flex items-center space-x-2 text-sm text-gray-600">
            <Calendar className="w-4 h-4" />
            <span>Resets in 8h 32m</span>
          </div>
        </div>
        <div className="space-y-3">
          {dailyTasks.map((task) => (
            <div
              key={task.id}
              className={`flex items-center justify-between p-4 rounded-xl ${
                task.completed
                  ? 'bg-green-50 border border-green-200'
                  : 'bg-gray-50 border border-gray-200'
              }`}
            >
              <div className="flex items-center space-x-4">
                {task.completed ? (
                  <CheckCircle2 className="w-6 h-6 text-green-600" />
                ) : (
                  <Circle className="w-6 h-6 text-gray-400" />
                )}
                <div>
                  <div className="mb-1">{task.title}</div>
                  <div className="text-sm text-gray-600">{task.description}</div>
                </div>
              </div>
              <div className="flex items-center space-x-2">
                <Star className="w-5 h-5 text-yellow-600 fill-yellow-600" />
                <span className="text-lg">+{task.reward}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Weekly Tasks */}
      <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl">Weekly Challenges</h2>
          <div className="flex items-center space-x-2 text-sm text-gray-600">
            <Calendar className="w-4 h-4" />
            <span>Resets in 4d 12h</span>
          </div>
        </div>
        <div className="space-y-4">
          {weeklyTasks.map((task) => (
            <div
              key={task.id}
              className="p-4 bg-indigo-50 border border-indigo-200 rounded-xl"
            >
              <div className="flex items-center justify-between mb-3">
                <div className="flex-1">
                  <div className="mb-1">{task.title}</div>
                  <div className="text-sm text-gray-600">{task.description}</div>
                </div>
                <div className="flex items-center space-x-2">
                  <Star className="w-5 h-5 text-yellow-600 fill-yellow-600" />
                  <span className="text-lg">+{task.reward}</span>
                </div>
              </div>
              <div className="flex items-center space-x-3">
                <div className="flex-1 h-2 bg-white rounded-full overflow-hidden">
                  <div
                    className="h-full bg-indigo-600 rounded-full"
                    style={{ width: `${(task.progress / task.total) * 100}%` }}
                  />
                </div>
                <div className="text-sm text-gray-600 whitespace-nowrap">
                  {task.progress}/{task.total}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      <div className="grid lg:grid-cols-2 gap-6">
        {/* Special Tasks */}
        <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
          <div className="flex items-center space-x-2 mb-4">
            <Gift className="w-6 h-6 text-purple-600" />
            <h2 className="text-xl">Special Rewards</h2>
          </div>
          <div className="space-y-3">
            {specialTasks.map((task) => (
              <div
                key={task.id}
                className="p-4 bg-gradient-to-r from-purple-50 to-pink-50 rounded-xl border border-purple-200"
              >
                <div className="flex items-start justify-between">
                  <div className="flex items-start space-x-3">
                    <div className="text-2xl">{task.icon}</div>
                    <div>
                      <div className="mb-1">{task.title}</div>
                      <div className="text-sm text-gray-600">{task.description}</div>
                    </div>
                  </div>
                  <div className="flex items-center space-x-1 text-purple-600">
                    <Star className="w-5 h-5 fill-purple-600" />
                    <span className="text-lg">+{task.reward}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Achievements */}
        <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
          <h2 className="text-xl mb-4">Achievements</h2>
          <div className="grid grid-cols-3 gap-4">
            {achievements.map((achievement) => (
              <div
                key={achievement.id}
                className={`p-4 rounded-xl text-center ${
                  achievement.unlocked
                    ? 'bg-gradient-to-r from-yellow-50 to-orange-50 border border-yellow-200'
                    : 'bg-gray-50 border border-gray-200 opacity-50'
                }`}
              >
                <div className="text-3xl mb-2">{achievement.icon}</div>
                <div className="text-sm">{achievement.name}</div>
              </div>
            ))}
          </div>
          <div className="mt-4 p-4 bg-yellow-50 rounded-xl text-center">
            <div className="text-sm text-gray-600">Achievements Unlocked</div>
            <div className="text-2xl text-yellow-600">
              {achievements.filter(a => a.unlocked).length} / {achievements.length}
            </div>
          </div>
        </div>
      </div>

      {/* Benefits */}
      <div className="bg-gradient-to-r from-indigo-600 to-purple-600 p-8 rounded-2xl text-white">
        <h2 className="text-2xl mb-4">Reputation Benefits</h2>
        <div className="grid md:grid-cols-3 gap-6">
          <div>
            <div className="text-3xl mb-2">⚡</div>
            <div className="text-lg mb-2">Priority Support</div>
            <p className="text-indigo-100">Get faster response times for your queries</p>
          </div>
          <div>
            <div className="text-3xl mb-2">🎁</div>
            <div className="text-lg mb-2">Exclusive Rewards</div>
            <p className="text-indigo-100">Access special competitions and bonuses</p>
          </div>
          <div>
            <div className="text-3xl mb-2">💰</div>
            <div className="text-lg mb-2">Higher Limits</div>
            <p className="text-indigo-100">Increased deposit and withdrawal limits</p>
          </div>
        </div>
      </div>
    </div>
  );
}
