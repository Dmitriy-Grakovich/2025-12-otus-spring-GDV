import { useEffect, useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import api from '@/lib/api';
import { Users, BookOpen, MessageSquare, TrendingUp } from 'lucide-react';

interface Stats {
  totalUsers: number;
  activeUsers: number;
  totalBooks: number;
  publishedBooks: number;
  booksOnModeration: number;
  totalReviews: number;
}

export function AdminPage() {
  const [stats, setStats] = useState<Stats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      const response = await api.get('/statistics/overview');
      setStats(response.data);
    } catch (error) {
      console.error('Ошибка загрузки статистики:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleExportBooks = async () => {
    try {
      await api.post('/batch/export-books');
      alert('Экспорт книг запущен успешно');
    } catch (error) {
      console.error('Ошибка экспорта:', error);
      alert('Ошибка при запуске экспорта');
    }
  };

  if (loading) {
    return <div className="text-center py-12">Загрузка...</div>;
  }

  if (!stats) {
    return <div className="text-center py-12">Нет доступа</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold">Панель администратора</h1>
        <Button onClick={handleExportBooks}>
          Экспорт книг в CSV
        </Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              Всего пользователей
            </CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.totalUsers}</div>
            <p className="text-xs text-muted-foreground">
              Активных: {stats.activeUsers}
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              Всего книг
            </CardTitle>
            <BookOpen className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.totalBooks}</div>
            <p className="text-xs text-muted-foreground">
              Опубликовано: {stats.publishedBooks}
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              На модерации
            </CardTitle>
            <TrendingUp className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.booksOnModeration}</div>
            <p className="text-xs text-muted-foreground">
              Требуют проверки
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              Всего отзывов
            </CardTitle>
            <MessageSquare className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.totalReviews}</div>
            <p className="text-xs text-muted-foreground">
              От читателей
            </p>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Быстрые действия</CardTitle>
          <CardDescription>Управление платформой</CardDescription>
        </CardHeader>
        <CardContent className="space-y-2">
          <Button variant="outline" className="w-full justify-start">
            Управление пользователями
          </Button>
          <Button variant="outline" className="w-full justify-start">
            Модерация книг
          </Button>
          <Button variant="outline" className="w-full justify-start">
            Управление жанрами
          </Button>
          <Button variant="outline" className="w-full justify-start">
            Просмотр логов
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}
