import { TrendingUp, Ticket, Trophy, Star, DollarSign, Activity } from 'lucide-react';

interface DashboardProps {
  user: any;
}

export function Dashboard({ user }: DashboardProps) {
  const stats = [
    {
      label: 'Account Balance',
      value: `$${user?.balance.toFixed(2)}`,
      change: '+12.5%',
      icon: DollarSign,
      color: '#22c55e',
    },
    {
      label: 'Reputation Score',
      value: user?.reputation,
      change: '+85',
      icon: Star,
      color: '#eab308',
    },
    {
      label: 'Active Tickets',
      value: '12',
      change: '+3 this week',
      icon: Ticket,
      color: '#646cff',
    },
    {
      label: 'Competitions',
      value: '5',
      change: '2 ending soon',
      icon: Trophy,
      color: '#ec4899',
    },
  ];

  const recentActivities = [
    {
      id: 1,
      type: 'lottery',
      title: 'Mega Jackpot Lottery',
      description: 'Purchased 3 tickets',
      time: '2 hours ago',
      icon: Ticket,
      color: '#646cff',
    },
    {
      id: 2,
      type: 'competition',
      title: 'Weekly Photo Contest',
      description: 'Submitted entry',
      time: '5 hours ago',
      icon: Trophy,
      color: '#ec4899',
    },
    {
      id: 3,
      type: 'task',
      title: 'Daily Login Bonus',
      description: 'Earned +50 reputation',
      time: '1 day ago',
      icon: Star,
      color: '#eab308',
    },
    {
      id: 4,
      type: 'deposit',
      title: 'Account Deposit',
      description: 'Added $100.00',
      time: '2 days ago',
      icon: DollarSign,
      color: '#22c55e',
    },
  ];

  const upcomingDraws = [
    {
      id: 1,
      name: 'Daily Lucky Draw',
      prize: '$5,000',
      time: '2h 15m',
      participants: 1234,
    },
    {
      id: 2,
      name: 'Weekly Mega Jackpot',
      prize: '$50,000',
      time: '3d 8h',
      participants: 5678,
    },
    {
      id: 3,
      name: 'Monthly Grand Prize',
      prize: '$500,000',
      time: '12d 5h',
      participants: 12450,
    },
  ];

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl mb-2 text-white">Welcome back, {user?.username}! 👋</h1>
        <p className="text-gray-400">Here's what's happening with your account</p>
      </div>

      {/* Stats Grid */}
      <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat) => {
          const Icon = stat.icon;
          return (
            <div
              key={stat.label}
              className="bg-[rgba(30,41,59,0.7)] backdrop-blur-xl border border-white/10 p-6 rounded-2xl hover:border-white/20 transition-all"
            >
              <div className="flex items-start justify-between mb-4">
                <div className="p-3 rounded-xl" style={{ backgroundColor: `${stat.color}20` }}>
                  <Icon className="w-6 h-6" style={{ color: stat.color }} />
                </div>
                <div className="flex items-center text-sm text-emerald-400">
                  <TrendingUp className="w-4 h-4 mr-1" />
                  <span className="text-xs">{stat.change}</span>
                </div>
              </div>
              <div className="text-2xl mb-1 text-white">{stat.value}</div>
              <div className="text-sm text-gray-400">{stat.label}</div>
            </div>
          );
        })}
      </div>

      <div className="grid lg:grid-cols-2 gap-6">
        {/* Recent Activity */}
        <div className="bg-[rgba(30,41,59,0.7)] backdrop-blur-xl border border-white/10 p-6 rounded-2xl">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-xl text-white">Recent Activity</h2>
            <Activity className="w-5 h-5 text-gray-400" />
          </div>
          <div className="space-y-4">
            {recentActivities.map((activity) => {
              const Icon = activity.icon;
              return (
                <div key={activity.id} className="flex items-start space-x-4">
                  <div className="p-2 rounded-lg" style={{ backgroundColor: `${activity.color}20` }}>
                    <Icon className="w-5 h-5" style={{ color: activity.color }} />
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="text-sm mb-1 text-gray-200">{activity.title}</div>
                    <div className="text-xs text-gray-400">{activity.description}</div>
                  </div>
                  <div className="text-xs text-gray-500 whitespace-nowrap">
                    {activity.time}
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Upcoming Draws */}
        <div className="bg-[rgba(30,41,59,0.7)] backdrop-blur-xl border border-white/10 p-6 rounded-2xl">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-xl text-white">Upcoming Draws</h2>
            <Ticket className="w-5 h-5 text-gray-400" />
          </div>
          <div className="space-y-4">
            {upcomingDraws.map((draw) => (
              <div
                key={draw.id}
                className="p-4 bg-white/5 border border-white/10 rounded-xl"
              >
                <div className="flex items-start justify-between mb-2">
                  <div>
                    <div className="mb-1 text-gray-200">{draw.name}</div>
                    <div className="text-sm text-gray-400">
                      {draw.participants.toLocaleString()} participants
                    </div>
                  </div>
                  <div className="text-right">
                    <div className="text-[#646cff]">{draw.prize}</div>
                    <div className="text-xs text-gray-500">{draw.time}</div>
                  </div>
                </div>
                <div className="mt-3 h-2 bg-black/30 rounded-full overflow-hidden">
                  <div
                    className="h-full bg-[#646cff]"
                    style={{ width: '65%' }}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="bg-[#646cff] p-8 rounded-2xl relative overflow-hidden">
        <div className="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-32 -mt-32"></div>
        <div className="absolute bottom-0 left-0 w-48 h-48 bg-white/10 rounded-full -ml-24 -mb-24"></div>
        <div className="relative z-10">
          <h2 className="text-2xl mb-4 text-white">Ready to get lucky?</h2>
          <p className="text-white/80 mb-6">
            Check out the latest lotteries and competitions or complete tasks to boost your
            reputation
          </p>
          <div className="flex flex-wrap gap-3">
            <button className="px-6 py-3 bg-white text-[#646cff] rounded-xl hover:shadow-lg transition-shadow">
              Browse Lotteries
            </button>
            <button className="px-6 py-3 bg-white/20 text-white border border-white/30 rounded-xl hover:bg-white/30 transition-colors">
              View Competitions
            </button>
            <button className="px-6 py-3 bg-white/20 text-white border border-white/30 rounded-xl hover:bg-white/30 transition-colors">
              Complete Tasks
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
